<template>
  <div class="page-container flex">
    <div class="left-panel p-6 overflow-y-auto">
      <h2 class="text-2xl font-bold mb-2">{{ knowledge.title }}</h2>
      <p class="text-gray-600 mb-4">{{ knowledge.description }}</p>

      <el-tabs v-model="currentResourceId" class="mb-4">
        <el-tab-pane
          v-for="res in knowledge.resources"
          :label="res.name"
          :name="res.id"
          :key="res.id"
        >
          <VideoPlay v-if="res.type === 'video'" :url="res.url" />
          <div v-else-if="res.type === 'text'" class="p-4 bg-white rounded shadow">
            <p>{{ res.content }}</p>
          </div>
          <div v-else class="text-gray-400">暂不支持该类型</div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <div class="right-panel flex flex-col">
      <ChatGPT />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import VideoPlay from './components/VideoPlay.vue'
import ChatGPT from './components/ChatGPT.vue'
import { useRoute } from 'vue-router'
const route = useRoute()
const knowledgeId = ref(route.params.id as string || '1')

const knowledge = reactive({
  title: '',
  description: '',
  resources: [] as Array<{
    id: string
    type: string
    name: string
    url?: string
    content?: string
  }>
})

const currentResourceId = ref('')
const currentResource = ref<typeof knowledge.resources[0] | null>(null)

async function fetchKnowledge() {
  try {
    knowledge.title = '神经网络基础'
    knowledge.description = '本模块介绍神经网络的基本结构与原理。'
    knowledge.resources = [
      {
        id: 'r1',
        type: 'video',
        name: '教学视频',
        url: ''
      },
      {
        id: 'r2',
        type: 'text',
        name: '学习笔记',
        content: '神经网络是一种模仿人脑的算法结构...' 
      }
    ]
    currentResourceId.value = knowledge.resources[0]?.id || ''
  } catch (e) {
    ElMessage.error('加载资源失败')
  }
}

watch(currentResourceId, (newId) => {
  currentResource.value = knowledge.resources.find(r => r.id === newId) || null
})

onMounted(() => {
  fetchKnowledge()
})
</script>

<style scoped>
.page-container {
  height: calc(95vh - 90px);
  display: flex;
}
.left-panel {
  width: 70%;
  background-color: #f9fafb;
}
.right-panel {
  width: 30%;
  border-left: 1px solid #e5e7eb;
  display: flex;
  flex-direction: column;
  height: 100%;
}
.chat-body::-webkit-scrollbar {
  width: 6px;
}
.chat-body::-webkit-scrollbar-thumb {
  background: #ccc;
  border-radius: 3px;
}
</style>