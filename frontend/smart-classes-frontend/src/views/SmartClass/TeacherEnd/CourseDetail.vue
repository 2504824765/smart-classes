<template>
  <div style="display: flex; flex-direction: column; gap: 4px;">
    <el-card shadow="never" class="mission-list">
      <div class="flex justify-between items-center mb-2">
      <ElButton
        type="primary"
        @click="push({ 
          path: '/course/detail/form', 
          query: { 
            classId: classId,
            redirect: route.fullPath 
          }
        })"
      >
        添加任务
      </ElButton>
        <ElButton type="danger" @click="deleteSelectedMissions">
          删除选中任务
        </ElButton>
      </div>

      <div style="height: 5px; color: black;"></div>

      <draggable v-model="missions" item-key="id" animation="200">
        <template #item="{ element }">
          <div class="flex items-start gap-2 mb-2">
            <el-checkbox
              :model-value="selectedMissionIds.includes(element.id)"
              @change="(val) => toggleSelection(element.id, !!val)"
            />
            <MissionCard :mission="element" />
          </div>
        </template>
      </draggable>
    </el-card>

    <!-- 资源文件列表 -->
    <el-card class="col-span-3 mt-4">
      <div class="mb-2 font-bold">已有资源文件</div>
      <div v-if="fileCards.length > 0" class="grid gap-3">
        <el-card
          v-for="file in fileCards"
          :key="file.id"
          shadow="hover"
          class="cursor-pointer hover:shadow-lg transition duration-200"
          body-style="padding: 12px"
        >
          <div class="flex justify-between items-center">
            <div class="flex items-center gap-2">
              <Icon icon="ep:document" class="text-blue-500" />
              <el-link :href="file.url" target="_blank" :underline="false">
                {{ file.name }}
              </el-link>
            </div>
            <el-button
              type="danger"
              icon="Delete"
              size="small"
              @click.stop="handleDelete(file.id)"
            >
              删除
            </el-button>
          </div>
        </el-card>
      </div>
      <el-empty v-else description="暂无资源文件" />
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

    <el-card style="min-height: 350px" body-style="padding: 16px;">
      <KnowledgeGraph v-if="classId !== undefined" :classId="classId" :key="graphKey" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import draggable from 'vuedraggable'
import MissionCard from './components/MissionCard.vue'
import { getClassMissionByCidApi, deleteClassMissionApi } from '@/api/classMission/index'
import type { ClassMission } from '@/api/classMission/types'
import { useRouter, useRoute } from 'vue-router'
import KnowledgeGraph from './components/KnowledgeGraph.vue'
import { uploadResourcesApi } from '@/api/oss/index'
import { ElMessage, ElMessageBox } from 'element-plus'
import { PREFIX } from '@/constants'
import type { UploadFile } from 'element-plus'
import { Resource, ResourceCreateDTO } from '@/api/resource/types'
import { addResourceApi, deleteResourceApi, getResourceByClassIdApi } from '@/api/resource/index'

const graphKey = ref(0)

const route = useRoute()
const { push } = useRouter()
const classId = ref<number>()

const missions = ref<ClassMission[]>([])

const uploading = ref(false)
const fileList = ref<UploadFile[]>([])

const selectedMissionIds = ref<number[]>([])

const toggleSelection = (id: number, checked: boolean) => {
  if (checked) {
    selectedMissionIds.value.push(id)
  } else {
    selectedMissionIds.value = selectedMissionIds.value.filter((mid) => mid !== id)
  }
}

const deleteSelectedMissions = async () => {
  if (selectedMissionIds.value.length === 0) {
    ElMessage.warning('请先选择要删除的任务')
    return
  }

  try {
    await Promise.all(selectedMissionIds.value.map((id) => deleteClassMissionApi(id)))
    ElMessage.success('删除成功')
    await fetchMissions()
    selectedMissionIds.value = []
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

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
    graphKey.value++
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

  await fetchFiles()
}

const fileCards = ref<
  { id: number; name: string; type: string; url: string }[]
>([])

const fetchFiles = async () => {
  if(!classId.value){
    console.log('classId is undefined')
    return
  }
  try {
    const res = await getResourceByClassIdApi(classId.value)
    console.log(res)
    fileCards.value = res.data.map((resource: Resource) => ({
      id: resource.id,
      name: resource.name,
      type: resource.type,
      url: PREFIX + resource.path,
    }))
  } catch (e) {
    console.error('资源获取失败', e)
  }
}

const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该文件吗？', '提示', {
      type: 'warning',
    })
    await deleteResourceApi(id)
    ElMessage.success('删除成功')
    await fetchFiles()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
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
