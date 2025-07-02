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
            
          </div>
          <div v-else-if="res.type === 'question'" class="text-gray-400">
            <QuestionCard
              v-for="(q, index) in questions"
              :key="index"
              v-model="answers[index]"
              :question="q.question"
              :options="q.options"
              :answer="q.answer"
              :showResult="submitted"
            />
            <el-button type="primary" @click="handleSubmit">提交</el-button>
          </div>
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
import QuestionCard from './components/QuestionCard.vue'
import { getResourceByClassIdApi } from '@/api/resource/index'
import type { Resource } from '@/api/resource/types'
import { PREFIX } from '@/constants/index'
import { createDifyGenerateQuestionRequest } from '@/api/dify/types'
import { generateQuestionApi } from '@/api/dify'

interface QuestionItem {
  question: string
  options: Record<string, string>  
  answer: string 
}

const route = useRoute()

const classId = Number(route.query.classId)
const knowledgeName = route.query.knowledgeName as string

const loading = ref(false)
const currentResourceId = ref('')
const questions = ref<QuestionItem[]>([])
const answers = ref<(string | null)[]>([])
const submitted = ref(false)

watch(currentResourceId, async (newId) => {
  const questionTabId = 'r3' 
  if (newId === questionTabId && questions.value.length === 0) {
    loading.value = true
    try {
      fetchQuestions()
    } finally {
      loading.value = false
    }
  }
})

const handleSubmit = () => {
  submitted.value = true
}

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

const currentResource = ref<(typeof knowledge.resources)[0] | null>(null)

async function fetchKnowledge() {
  try {
    knowledge.title = knowledgeName
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
      },
      {
        id: 'r3',
        type: 'question',
        name: '练习题'
      }
    ]
    currentResourceId.value = knowledge.resources[0]?.id || ''
  } catch (e) {
    ElMessage.error('加载资源失败')
  }
}

const fetchQuestions = async () => {
  if (!classId) {
    console.warn('classId 不存在，跳过图谱加载')
    return
  }
  const resourcesRes = await getResourceByClassIdApi(classId)
  console.log('resourcesRes', resourcesRes)
  const resources = resourcesRes.data
  if (!resourcesRes) return
  const urls = resources.map((res: Resource) => PREFIX + res.path.replace(/^\/+/, ''))
  console.log('urls', urls)
  const requestBody = createDifyGenerateQuestionRequest(urls,knowledgeName,5)
  console.log('requestBody', requestBody)
  const urlRes = await generateQuestionApi(requestBody)
  console.log('urlRes', urlRes)
  const res = await fetch(urlRes.data)
    .then(r => r.json())
    .catch(err => {
      console.error('下载题目 JSON 失败', err)
      return []
    })
  console.log('res', res)
  questions.value = res  
  answers.value = Array(res.length).fill(null) 
}

watch(currentResourceId, (newId) => {
  currentResource.value = knowledge.resources.find((r) => r.id === newId) || null
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
