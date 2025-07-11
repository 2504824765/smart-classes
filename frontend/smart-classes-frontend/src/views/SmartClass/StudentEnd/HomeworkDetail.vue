<template>
  <div class="homework-detail">
    <el-page-header content="作业详情" @back="router.back" style="margin-bottom: 16px" />

    <el-card>
      <template #header>
        <div class="flex items-center justify-between">
          <h2 class="text-xl font-bold"
            >{{ classMission?.classes.name }} - {{ classMission?.type }}</h2
          >
          <el-tag type="success" v-if="studentMission?.done">已完成</el-tag>
          <el-tag type="warning" v-else>未完成</el-tag>
        </div>
      </template>

      <el-descriptions title="作业信息" :column="2" border>
        <el-descriptions-item label="课程">{{ classMission?.classes.name }}</el-descriptions-item>
        <el-descriptions-item label="作业类型">{{ classMission?.type }}</el-descriptions-item>
        <el-descriptions-item label="截止时间">{{ classMission?.deadline }}</el-descriptions-item>
        <el-descriptions-item label="提交方式">{{
          classMission?.submitMethod
        }}</el-descriptions-item>
        <el-descriptions-item label="作业说明" :span="2">
          {{ classMission?.description }}
        </el-descriptions-item>
      </el-descriptions>

      <el-divider>学生完成情况</el-divider>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="学生">{{ studentMission?.student.name }}</el-descriptions-item>
        <el-descriptions-item label="得分">{{
          studentMission?.score ?? '未评分'
        }}</el-descriptions-item>
      </el-descriptions>

      <!-- 可选：上传入口、文件查看等 -->
      <div class="mt-4">
        <!-- 上传区域 -->
        <el-upload
          :show-file-list="false"
          :limit="1"
          accept=".pdf,.doc,.docx,.zip,.rar,.txt"
          :before-upload="() => false"
          :on-change="handleFileChange"
        >
          <el-button type="primary">选择文件</el-button>
        </el-upload>

        <!-- 文件列表展示 -->
        <div class="mt-2">
          <el-card
            v-for="file in fileList"
            :key="file.name"
            class="mb-2"
            shadow="hover"
          >
            <div class="flex justify-between items-center">
              <div>
                <el-icon class="mr-2"><Document /></el-icon>
                {{ file.name }}
              </div>
              <el-button
                type="primary"
                size="small"
                text
                @click="handleDownload(file)"
              >
                下载
              </el-button>
            </div>
          </el-card>
          <el-empty v-if="!fileList.length" description="暂无文件" />
        </div>

        <!-- 提交按钮 -->
        <el-button type="success" class="mt-2" @click="handleSubmit">提交文件</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'
import { onMounted, ref } from 'vue'
import { ClassMission } from '@/api/classMission/types'
import { getClassMissionByIdApi } from '@/api/classMission/index'
import { getStudentMissionById } from '@/api/studentMission/index'
import { StudentMission } from '@/api/studentMission/types'
import { uploadResourcesApi } from '@/api/oss/index'
import { updateStudentMission } from '@/api/studentMission/index'
import { UploadFile, ElMessage } from 'element-plus'
import { PREFIX } from '@/constants'
const route = useRoute()
const router = useRouter()

const classMission = ref<ClassMission>()
const studentMission = ref<StudentMission>()

const queryDate = async () => {
  const missionId = Number(route.query.missionId)
  const studentMissionId = Number(route.query.studentMissionId)
  const classMissionRes = await getClassMissionByIdApi(missionId)
  if (classMissionRes.code === 200) {
    classMission.value = classMissionRes.data
  }
  const studentMissionRes = await getStudentMissionById(studentMissionId)
  if (studentMissionRes.code === 200) {
    studentMission.value = studentMissionRes.data
    console.log('res:',studentMissionRes)
    console.log('studentMission:',studentMission.value)
    loadHistoryFile()
  }
}

const handleDownload = (file: File | { url: string; name: string } | any) => {
  let url: string
  let name: string

  // 已上传的 OSS 文件（带 url）
  if (file.url && file.name) {
    url = file.url
    name = file.name
  }
  // 上传前的本地文件（raw File 对象）
  else if (file.raw instanceof File) {
    url = URL.createObjectURL(file.raw)
    name = file.name || file.raw.name
  } else {
    ElMessage.warning('无法识别的文件对象')
    return
  }

  const a = document.createElement('a')
  a.href = url
  a.download = name
  a.style.display = 'none'
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)

  // 清理 blob URL
  if (file.raw instanceof File) {
    URL.revokeObjectURL(url)
  }
}

const fileList = ref<UploadFile[]>([])

// 页面初始化时加载历史文件
const loadHistoryFile = () => {
  const path = studentMission.value?.reportUrl
  if (path) {
    fileList.value = [
      {
        name: path.split('/').pop() || '已提交文件',
        url: PREFIX + path, // 拼接完整URL
        status: 'success'
      } as UploadFile
    ]
  }
}

// 选择新文件（覆盖旧的 fileList）
const handleFileChange = (uploadFile: UploadFile) => {
  fileList.value = [uploadFile]
}

// 提交按钮逻辑
const handleSubmit = async () => {
  const fileRaw = fileList.value[0]?.raw as File | undefined
  if (!fileRaw) {
    ElMessage.warning('请先选择一个文件')
    return
  }
  if (!studentMission.value) {
    ElMessage.warning('请先获取学生作业信息')
    return
  }

  try {
    // 1. 上传文件
    const uploadRes = await uploadResourcesApi(fileRaw, '学生任务')
    const fullPath = uploadRes.data
    const relativePath = fullPath.replace(PREFIX, '')
    if (!studentMission.value?.reportUrl) {
      ElMessage.success('文件上传并资源创建成功')
    } else {
      studentMission.value.reportUrl = relativePath
      ElMessage.success('文件上传并资源更新成功')
    }
    console.log(studentMission.value)
    await updateStudentMission(studentMission.value)
  } catch (err) {
    ElMessage.error('上传失败，请稍后重试')
  }
}

onMounted(() => {
  queryDate()
})
</script>

<style scoped>
.homework-detail {
  padding: 20px;
}
</style>
