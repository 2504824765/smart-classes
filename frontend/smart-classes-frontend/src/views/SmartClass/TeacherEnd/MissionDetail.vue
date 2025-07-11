<template>
  <div class="mission-detail">
    <el-page-header content="任务详情" @back="router.back" />

    <!-- 任务详情 -->
    <el-card class="mb-4" style="margin-top: 10px">
      <template #header>
        <span>任务信息</span>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="任务类型">{{ mission?.type }}</el-descriptions-item>

        <!-- 截止时间：可编辑 -->
        <el-descriptions-item label="截止时间">
          <el-date-picker
            v-model="editableDeadline"
            type="datetime"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            :disabled-date="disabledPastDate"
            placeholder="选择截止时间"
            style="width: 100%"
          />
          <el-button
            size="small"
            type="primary"
            class="mt-1"
            @click="updateDeadline"
            :disabled="!editableDeadline"
          >
            更新
          </el-button>
        </el-descriptions-item>

        <el-descriptions-item label="提交方式">{{ mission?.submitMethod }}</el-descriptions-item>
        <el-descriptions-item label="总分">{{ mission?.score }}</el-descriptions-item>
        <el-descriptions-item label="任务描述" :span="2">
          {{ mission?.description }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card class="mb-4">
      <template #header>
        <div class="flex justify-between items-center">
          <span>相关资源</span>
        </div>
      </template>

      <div v-if="fileCards.length">
        <div v-for="file in fileCards" :key="file.id" class="mb-2 flex items-center gap-2">
          <FileDisplay :url="file.url" />
          <el-button
            type="danger"
            icon="Delete"
            size="small"
            style="width: 40px;"
            @click="deleteFile(file)"
          >
            删除
          </el-button>
        </div>
      </div>

      <div v-else>
        <el-empty description="暂无资源" />
      </div>

      <el-upload
        class="upload-demo"
        drag
        :action="''"
        :auto-upload="false"
        :on-change="handleFileChange"
        :file-list="[]"
        :multiple="true"
      >
        <el-icon><upload-filled /></el-icon>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
      </el-upload>

      <el-button type="primary" class="mt-2" @click="handleUpload" :loading="uploading">
        上传文件
      </el-button>
    </el-card>

    <!-- 学生完成情况 -->
    <el-card>
      <template #header>
        <span>学生完成情况</span>
      </template>
      <el-row :gutter="20">
        <el-col
          :span="4"
          v-for="studentMission in studentMissions"
          :key="studentMission.id"
          @click="viewStudentDetail(studentMission.id)"
        >
          <div
            class="student-card"
            :class="{ done: studentMission.isDone, undone: !studentMission.isDone }"
          >
            <el-avatar :size="60">{{ studentMission.student.name.slice(0, 1) }}</el-avatar>
            <div class="student-name">{{ studentMission.student.name }}</div>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getStudentMissionByMission } from '@/api/studentMission/index'
import { getClassMissionByIdApi, updateClassMissionApi } from '@/api/classMission/index'
import type { ClassMission, ClassMissionUpdateDTO } from '@/api/classMission/types'
import { uploadResourcesApi } from '@/api/oss/index'
import { createClassMissionResource, deleteClassMissionResource } from '@/api/classMissionResource/index'
import FileDisplay from './components/FileDisplay.vue'
import { PREFIX } from '@/constants'
import { StudentMission } from '@/api/studentMission/types'
import { getAllClassMissionResourcesByClassMissionId } from '@/api/classMissionResource'
import { ElMessage, ElMessageBox, UploadFile } from 'element-plus'

const route = useRoute()
const router = useRouter()
const { push } = useRouter()

const mission = ref<ClassMission>()
const fileCards = ref<{ id:number; name: string; url: string }[]>([])
const studentMissions = ref<(StudentMission & { isDone: boolean })[]>([])

const editableDeadline = ref<string>('')

const pendingResources = ref<
  {
    name: string
    type: string
    description: string
    file: File
  }[]
>([])

const uploading = ref(false)

// 初始赋值
watch(
  () => mission.value,
  (val) => {
    if (val?.deadline) editableDeadline.value = val.deadline
  },
  { immediate: true }
)

const disabledPastDate = (time: Date) => {
  return time.getTime() < Date.now() - 86400000 // 禁用今天以前
}

const updateDeadline = async () => {
  if(!mission.value){ 
    ElMessage.error('任务不存在')
    return
  }
  try {
    const updatedMission: ClassMissionUpdateDTO = {
      id: mission.value.id,
      deadline: editableDeadline.value
    }
    console.log(updatedMission)
    const res = await updateClassMissionApi(updatedMission)
    if (res.code === 200) {
      ElMessage.success('截止时间更新成功')
    } else {
      throw new Error('更新失败')
    }
  } catch (err) {
    ElMessage.error('更新失败，请稍后重试')
  }
}

const handleFileChange = (uploadFile: UploadFile) => {
  const rawFile = uploadFile.raw!
  const fileName = rawFile.name
  const fileType = fileName.split('.').pop() || ''

  pendingResources.value.push({
    name: fileName,
    type: fileType,
    description: fileName,
    file: rawFile
  })

  ElMessage.success(`已添加到待上传列表：${fileName}`)
}

const handleUpload = async () => {
  if (pendingResources.value.length === 0) {
    ElMessage.warning('没有待上传的文件')
    return
  }
  if(!mission.value){
    ElMessage.error('任务不存在')
    return
  }

  uploading.value = true

  try {
    for (const resFile of pendingResources.value) {
      // 上传文件到 OSS
      const uploadRes = await uploadResourcesApi(resFile.file, resFile.name)
      const relativePath = uploadRes.data.replace(PREFIX, '')

      const newRes = {
        path: relativePath,
        classMissionId: mission.value.id
      }

      const savedRes = await createClassMissionResource(newRes)
      console.log('savedRes', savedRes)
      fileCards.value.push({
        id: savedRes.data.id,
        name: resFile.name,
        url: PREFIX + relativePath
      })
    }

    ElMessage.success('所有资源上传成功')
    pendingResources.value = []
  } catch (err) {
    console.error(err)
    ElMessage.error('上传失败')
  } finally {
    uploading.value = false
  }
}

const deleteFile = async (file: { id: number; name: string }) => {
  try {
    await ElMessageBox.confirm(`确定删除资源 ${file.name}？`, '警告', { type: 'warning' })
    await deleteClassMissionResource(file.id)
    fileCards.value = fileCards.value.filter((f) => f.id !== file.id)
    ElMessage.success('删除成功')
  } catch {
    ElMessage.info('已取消删除')
  }
}

onMounted(async () => {
  const missionId = Number(route.query.id)
  const res = await getClassMissionByIdApi(missionId)
  console.log(res.data)
  mission.value = res.data

  // 获取资源文件（TODO: 请替换为实际资源接口）
  if (mission.value) {
    const resourceRes = await getAllClassMissionResourcesByClassMissionId(mission.value.id)
    console.log(resourceRes.data)
    if (Array.isArray(resourceRes.data)) {
      fileCards.value = resourceRes.data.map((item: any) => ({
        id: item.id,
        url: PREFIX + item.path, 
        name: item.name || '未命名资源'
      }))
      console.log('files',fileCards.value)
    } else {
      fileCards.value = []
    }
  }

  // 获取所有学生完成情况（已适配接口）
  const studentRes = await getStudentMissionByMission(missionId)
  console.log(studentRes.data)
  studentMissions.value = studentRes.data.map((s) => ({
    ...s,
    isDone: s.isDone
  }))
  console.log(studentMissions.value)
  console.log(missionId)
})

const viewStudentDetail = (studentMissionId: number) => {
  console.log(studentMissionId)
  if(!studentMissionId){
    return
  } 
  push({ path: '/course/detail/studentDetail', query: { studentMissionId: studentMissionId} })
}
</script>

<style scoped>  
.mission-detail {
  padding: 20px;
}

.mb-4 {
  margin-bottom: 20px;
}

.student-card {
  text-align: center;
  padding: 15px;
  border-radius: 8px;
  cursor: pointer;
  transition: 0.3s;
  background-color: #fdf6ec;
}

.student-card.done {
  background-color: #e1f3d8;
}

.student-card.undone {
  background-color: #fff7e6;
}

.student-card:hover {
  box-shadow: 0 0 8px rgba(0, 0, 0, 0.1);
}

.student-name {
  margin-top: 10px;
  font-weight: 500;
}
</style>
