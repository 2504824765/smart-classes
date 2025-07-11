<template>
  <div class="p-6">
    <el-page-header content="学生作业详情" @back="router.back" class="mb-4" />

    <el-card>
      <template #header>
        <div class="flex justify-between items-center">
          <h2 class="text-lg font-bold">学生作业信息</h2>
          <el-tag :type="studentMission?.isDone ? 'success' : 'warning'">
            {{ studentMission?.isDone ? '已完成' : '未完成' }}
          </el-tag>
        </div>
      </template>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="完成情况">
          {{ studentMission?.isDone ? '是' : '否' }}
        </el-descriptions-item>
        <el-descriptions-item label="激活状态">
          {{ studentMission?.isActive ? '是' : '否' }}
        </el-descriptions-item>
        <el-descriptions-item label="得分">
          <el-input-number
            v-model="editableScore"
            :min="0"
            :max="100"
            size="small"
            controls-position="right"
          />
          <el-button type="primary" size="small" class="ml-2" @click="updateScore">
            确认
          </el-button>
        </el-descriptions-item>
      </el-descriptions>

      <el-divider>作业文件区域</el-divider>
      <div class="grid grid-cols-2 gap-4">
        <!-- 模板文件卡片 -->
        <el-card class="text-center" shadow="hover">
            <div class="font-bold mb-2">模板文件</div>
            <el-empty v-if="!templateFile.url" description="暂无模板" />
            <div v-else class="flex justify-between items-center border rounded px-3 py-2 bg-gray-50">
            <div class="truncate">{{ templateFile.name }}</div>
            <el-button type="primary" size="small" @click="download(templateFile)">下载</el-button>
            </div>
        </el-card>

        <!-- 学生文件卡片 -->
        <el-card class="text-center" shadow="hover">
            <div class="font-bold mb-2">学生文件</div>
            <el-empty v-if="!studentFile.url" description="暂无提交文件" />
            <div v-else class="flex justify-between items-center border rounded px-3 py-2 bg-gray-50">
            <div class="truncate">{{ studentFile.name }}</div>
            <el-button type="success" size="small" @click="download(studentFile)">下载</el-button>
            </div>
        </el-card>
      </div>

      <el-button type="warning" @click="handleAutoGrade" :loading="grading" style="margin-top: 10px;">智能批改</el-button>

      <el-divider>智能批改结果</el-divider>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="评分">
          {{ aiScore || '暂无' }}
          <el-button
            v-if="aiScore"
            size="small"
            type="success"
            class="ml-2"
            @click="applyAiScore"
          >
            应用
          </el-button>
        </el-descriptions-item>
        <el-descriptions-item label="评语">{{ aiComment || '暂无' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getStudentMissionById } from '@/api/studentMission'
import { getAllClassMissionResourcesByClassMissionId } from '@/api/classMissionResource'
import { StudentMission } from '@/api/studentMission/types'
import { commentReportApi } from '@/api/dify'
import { createDifyReportCommentRequest } from '@/api/dify/types'
import { updateStudentMission } from '@/api/studentMission'
import { PREFIX } from '@/constants'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const studentMission = ref<StudentMission>()
const templateFile = ref<{ url: string; name: string }>({ url: '', name: '' })
const studentFile = ref<{ url: string; name: string }>({ url: '', name: '' })
const aiScore = ref('')
const aiComment = ref('')
const editableScore = ref<number | null>(null)

const updateScore = async () => {
  if (studentMission.value) {
    studentMission.value.score = editableScore.value ?? 0
    const res = await updateStudentMission(studentMission.value)
    if (res.code === 200) {
      ElMessage.success('得分已更新')
    } else {
      ElMessage.error('更新失败')
    }
  }
}

const applyAiScore = async () => {
  if (!studentMission.value || !aiScore.value) return
  studentMission.value.score = Number(aiScore.value)
  editableScore.value = Number(aiScore.value)
  console.log(editableScore)
  const res = await updateStudentMission(studentMission.value)
  if (res.code === 200) {
    ElMessage.success('AI评分已应用')
  } else {
    ElMessage.error('更新失败')
  }
}

const loadData = async () => {
  const studentMissionId = Number(route.query.studentMissionId)
  console.log(route.query.studentMissionId)
  if(!studentMissionId) {
    ElMessage.error('学生作业ID无效')
    return
  }

  const res = await getStudentMissionById(studentMissionId)
  console.log(res)
  if (res.code === 200) {
    studentMission.value = res.data

    if (studentMission.value?.score !== null) {
      editableScore.value = studentMission.value.score
    }

    if(studentMission.value?.aiCommentUrl) {
      const response = await fetch(PREFIX + studentMission.value?.aiCommentUrl)
      if (!response.ok) {
        throw new Error('下载 AI 评语失败')
      }
      const result = await response.json()

      aiComment.value = result.comment || '无评语'
      aiScore.value = result.score || '无评分'
    }

    // 设置学生文件
    const fileUrl = res.data.reportUrl
    if (fileUrl) {
      studentFile.value = {
        url: PREFIX + fileUrl,
        name: '学生作业.docx'
      }
    }

    // 获取模板文件（仅第一个）
    const missionId = res.data.classMission.id
    const templateRes = await getAllClassMissionResourcesByClassMissionId(missionId)
    if (templateRes.code === 200 && Array.isArray(templateRes.data) && templateRes.data.length > 0) {
      templateFile.value = {
        url: PREFIX + templateRes.data[0].path,
        name: templateRes.data[0].name || '模板文件'
      }
    }
  }
}

const download = (file: { url: string; name: string }) => {
  const link = document.createElement('a')
  link.href = file.url
  link.download = file.name
  link.click()
}

// 加载状态与结果
const grading = ref(false)

// 智能批改处理逻辑
const handleAutoGrade = async () => {
  if (!templateFile.value.url || !studentFile.value.url) {
    ElMessage.warning('请确保模板文件已选择，学生文件不为空')
    return
  }

  await ElMessageBox.alert('批改时间较长，请耐心等待', '提示', {
    confirmButtonText: '继续',
    type: 'info'
  })

  grading.value = true
  aiScore.value = ''
  aiComment.value = ''

  try {
    const request = createDifyReportCommentRequest(studentFile.value.url, templateFile.value.url)
    const res = await commentReportApi(request)
    const downloadUrl = res.data
    if(!studentMission.value){
      ElMessage.error('学生作业不存在')
      return
    }
    studentMission.value.aiCommentUrl = downloadUrl.replace(PREFIX,'')
    await updateStudentMission(studentMission.value)
    // 使用 fetch 获取 JSON 文件内容
    const response = await fetch(downloadUrl)
    if (!response.ok) {
      throw new Error('下载 AI 评语失败')
    }
    const result = await response.json()

    console.log('解析结果:', result)
    console.log(result)
    aiComment.value = result.comment || '无评语'
    aiScore.value = result.score || '无评分'

    ElMessage.success('批改完成')
  } catch (err) {
    ElMessage.error('批改失败，请稍后重试')
  } finally {
    grading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.el-descriptions {
  margin-top: 12px;
}
</style>
