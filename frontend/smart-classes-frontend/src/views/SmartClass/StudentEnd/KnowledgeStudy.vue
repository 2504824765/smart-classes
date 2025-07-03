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
          <div v-else-if="res.type === 'text'" class="p-4 bg-white rounded shadow"> </div>
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

  <el-dialog
    v-model="loading"
    title="题目生成中"
    width="300px"
    :close-on-click-modal="false"
    :show-close="true"
    @close="cancelRequest"
  >
    <p>正在生成题目，请稍候...</p>
    <template #footer>
      <el-button type="danger" @click="cancelRequest">取消生成</el-button>
    </template>
  </el-dialog>
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
import { ElMessageBox, ElLoading } from 'element-plus'

let controller: AbortController | null = null
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

  loading.value = true
  controller = new AbortController()

  const loadingInstance = ElLoading.service({
    lock: true,
    text: '正在生成题目...',
    background: 'rgba(0, 0, 0, 0.3)'
  })

  try {
    const resourcesRes = await getResourceByClassIdApi(classId)
    const resources = resourcesRes.data
    const urls = resources.map((res: Resource) => PREFIX + res.path.replace(/^\/+/, ''))

    const requestBody = createDifyGenerateQuestionRequest(urls, knowledgeName, 5)

    const urlRes = await generateQuestionApi(requestBody)

    // fetch JSON 文件并处理格式
    let rawText = await fetch(urlRes.data, { signal: controller.signal }).then((r) => r.text())

    // 清理掉前缀 “```json”和结尾“```” —— 如果有
    rawText = rawText.trim()
    rawText = rawText.replace(/^```json/, '').replace(/```$/, '')
    console.log('原始题目 JSON 文本：', rawText)
    const res = JSON.parse(rawText)

    questions.value = res
    answers.value = Array(res.length).fill(null)
  } catch (err: any) {
    if (err.name === 'AbortError') {
      console.warn('题目请求被用户中止')
    } else {
      console.error('题目生成失败', err)
    }
  } finally {
    loadingInstance.close()
    loading.value = false
  }
}

const cancelRequest = () => {
  if (controller) {
    controller.abort()
  }
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
