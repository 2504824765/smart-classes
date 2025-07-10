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
          <div v-if="res.type === 'text'" class="p-4 bg-white rounded shadow"> 
            <el-card class="mb-4 p-4 bg-white rounded shadow">
              <div v-html="compiledMarkdown" class="prose prose-sm max-w-none" />
            </el-card>

            <!-- 下：笔记编辑卡片 -->
            <el-card class="p-4 bg-white rounded shadow">
              <div class="mb-2 font-bold text-lg">我的笔记</div>
              <RichTextEditor v-model="noteContent" />
              <el-button type="primary" class="mt-2" @click="saveNote">保存笔记</el-button>
            </el-card>
          </div>
          <div v-else-if="res.type === 'question'" class="text-gray-400">
            <ElButton type="primary" :disabled="questions.length > 0" @click="createQuestions(1)" >生成题目</ElButton>
            <ElButton type="primary" :disabled="questions.length === 0" @click="createQuestions(2)">刷新题目</ElButton>
            <QuestionCard
              v-for="(q, index) in questions"
              :key="index"
              v-model="answers[index]"
              :question="q.question"
              :options="q.options"
              :answer="q.answer"
              :showResult="submitted"
            />
            <el-button type="primary" :disabled="questions.length === 0" @click="handleSubmit">提交</el-button>
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
import { ref, reactive, onMounted, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import ChatGPT from './components/ChatGPT.vue'
import { useRoute } from 'vue-router'
import QuestionCard from './components/QuestionCard.vue'
import { getResourceByClassIdApi } from '@/api/resource/index'
import type { Resource } from '@/api/resource/types'
import { PREFIX } from '@/constants/index'
import { createDifyGenerateQuestionRequest } from '@/api/dify/types'
import { generateQuestionApi } from '@/api/dify'
import { ElMessageBox, ElLoading } from 'element-plus'
import RichTextEditor from './components/RichTextEditor.vue'
import { DifyChatRequest } from '@/api/dify/types'
import { fetchDifyAnswerStream } from '@/api/dify/index'
import html2pdf from 'html2pdf.js'
import MarkdownIt from 'markdown-it'

const md = new MarkdownIt()

let controller: AbortController | null = null
interface QuestionItem {
  question: string
  options: Record<string, string>
  answer: string
}

const questionToAsk = ref('')

function sendToChatGPT() {
  questionToAsk.value = '请介绍神经网络的基本结构'
}

const route = useRoute()

const classId = Number(route.query.classId)
const knowledgeName = route.query.knowledgeName as string
const noteContent = ref('<p></p>')

const loading = ref(false)
const currentResourceId = ref('')
const questions = ref<QuestionItem[]>([])
const answers = ref<(string | null)[]>([])
const submitted = ref(false)

const aiMarkdown = ref('')
const currentNodeName = ref('')
const generating = ref(false)

const getLocalCacheKey = (nodeName: string) => `ai-intro-${nodeName}`

const loadNodeIntroduction = async (nodeName: string) => {
  if (currentNodeName.value === nodeName) return // 避免重复加载
  currentNodeName.value = nodeName
  aiMarkdown.value = ''
  generating.value = true

  // ✅ 若已有 controller，先中断前一次流
  if (controller) {
    controller.abort()
  }

  controller = new AbortController()

  const cacheKey = getLocalCacheKey(nodeName)
  const cached = localStorage.getItem(cacheKey)
  if (cached) {
    console.log('从缓存中获取AI内容')
    aiMarkdown.value = cached
    generating.value = false
    return
  }

  const question = `请你详细介绍与这个节点有关的知识，并且给出参考文献链接：${nodeName}`
  let aiContent = ''
  console.log(question)
  const request: DifyChatRequest = {
    inputs: {},
    query: question,
    response_mode: 'streaming',
    user: 'test-user',
    auto_generate_name: false,
    files: []
  }

  try {
    await fetchDifyAnswerStream(
      request,
      (chunk) => {
        if (chunk.event === 'message') {
          aiContent += chunk.answer || ''
          aiMarkdown.value = aiContent
        }
      },
      controller.signal
    )
    localStorage.setItem(cacheKey, aiContent)
  } catch (e) {
    ElMessage.error('获取AI内容失败')
  } finally {
    generating.value = false
  }
}


const createQuestions = async (method:number) => {
  if (method === 1) {
    if(questions.value.length > 0) {
      ElMessage.warning('已生成题目，请刷新页面重新生成')
      return
    }else{
      if (questions.value.length === 0) {
        loading.value = true
        try {
          fetchQuestions()
        } finally {
          loading.value = false
        }
      }
    }
  }
  else if (method === 2) {
    if (questions.value.length > 0) {
      questions.value = []
    }
    loading.value = true
    try {
      fetchQuestions()
    } finally {
      loading.value = false
    }
  }else {
    ElMessage.error('生成题目失败')
  }
}

const compiledMarkdown = computed(() => md.render(aiMarkdown.value)) // 渲染为 HTML

const saveNote = () => {
  const el = document.createElement('div')
  el.innerHTML = noteContent.value

  html2pdf()
    .set({
      margin: 0.5,
      filename: '我的笔记.pdf',
      image: { type: 'jpeg', quality: 0.98 },
      html2canvas: {},
      jsPDF: { unit: 'in', format: 'a4', orientation: 'portrait' }
    })
    .from(el)
    .save()
}


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
        type: 'text',
        name: '学习笔记',
        content: '神经网络是一种模仿人脑的算法结构...'
      },
      {
        id: 'r2',
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

  const confirmed = await ElMessageBox.confirm(
    '请确认资源文件存在，否则无法生成题目，是否继续？',
    '确认生成题目',
    {
      confirmButtonText: '继续',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).catch(() => false)

  ElMessage.info('题目生成时间较长，请耐心等待...')

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
    const limitedResources = resources.slice(0, 8)
    const urls = limitedResources.map((res: Resource) => PREFIX + res.path.replace(/^\/+/, ''))

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

onMounted(async () => {
  await fetchKnowledge()
  await loadNodeIntroduction(knowledgeName) 
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
