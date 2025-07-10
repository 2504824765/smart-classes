<template>
  <el-card shadow="never" class="mission-list">
    <ElButton
      type="primary"
      style="margin-bottom: 10px"
      @click="push({ path: '/course/detail/form' })"
      >添加任务</ElButton
    >
    <draggable v-model="missions" item-key="id" animation="200">
      <template #item="{ element }">
        <MissionCard :mission="element" />
      </template>
    </draggable>
  </el-card>

  <!-- 上传资源卡片 -->
  <el-card class="col-span-3">
    <div class="mb-2 font-bold">上传资源</div>
    <el-upload
      class="upload-demo"
      drag
      :action="''"
      :auto-upload="false"
      :on-change="handleFileChange"
      :file-list="fileList"
      :multiple="true"
    >
      <el-icon><upload-filled /></el-icon>
      <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
    </el-upload>
    <el-button class="mt-4" type="primary" @click="uploadResource" :loading="uploading">
      上传文件
    </el-button>
  </el-card>

  <el-card>
    <KnowledgeGraph v-if="classId !== undefined" :classId="classId" />
  </el-card>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import draggable from 'vuedraggable'
import MissionCard from './components/MissionCard.vue'
import { getClassMissionByCidApi } from '@/api/classMission/index'
import type { ClassMission } from '@/api/classMission/types'
import { useRouter, useRoute } from 'vue-router'
import KnowledgeGraph from './components/KnowledgeGraph.vue'
import { uploadResourcesApi } from '@/api/oss/index'
import { ElMessage } from 'element-plus'
import { PREFIX } from '@/constants'
import type { UploadFile } from 'element-plus'
import { ResourceCreateDTO } from '@/api/resource/types'
import { addResourceApi } from '@/api/resource/index'

const route = useRoute()
const { push } = useRouter()
const classId = ref<number>()

const missions = ref<ClassMission[]>([])

const uploading = ref(false)
const fileList = ref<UploadFile[]>([])

const handleFileChange = (uploadFile: UploadFile, uploadFiles: UploadFile[]) => {
  fileList.value = uploadFiles
}

const uploadResource = async () => {
  if (fileList.value.length === 0) {
    ElMessage.warning('请先选择文件')
    return
  }

  if (!classId.value) {
    ElMessage.warning('请先选择课程')
    return
  }

  uploading.value = true

  try {
    for (const fileItem of fileList.value) {
      const file = fileItem.raw as File
      const res = await uploadResourcesApi(file, '')
      console.log(res)
      const url = res.data.replace(PREFIX, '')

      const resource: ResourceCreateDTO = {
        name: file.name,
        path: url,
        type: file.type,
        description: '',
        classId: classId.value
      }

      await addResourceApi(resource)
    }

    ElMessage.success('所有资源上传成功')
    fileList.value = [] // 清空文件列表（可选）
  } catch (err) {
    console.error(err)
    ElMessage.error('上传过程中发生错误')
  } finally {
    uploading.value = false
  }
}

const fetchMissions = async () => {
  if (!route.query.classId) return

  classId.value = Number(route.query.classId)
  const res = await getClassMissionByCidApi(classId.value)
  missions.value = res.data
}

onMounted(fetchMissions)
</script>

<style scoped>
.mission-list {
  padding: 16px;
}
.upload-demo {
  width: 100%;
  padding: 10px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  text-align: center;
}
</style>
