<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

// 引入自定义视频组件
import VideoPlay from './components/VideoPlay.vue'

const route = useRoute()
const knowledgeId = ref(route.params.id as string)

const knowledge = reactive({
  title: '',
  description: '',
  resources: [] as Array<{
    id: string
    type: string
    name: string
    url?: string
    content?: string
  }>,
})

const currentResourceId = ref('')
// const currentResource = ref<typeof knowledge.resources[0] | null>(null)

// async function fetchKnowledge() {
//   try {
//     const res = await fetch(`/api/knowledge/${knowledgeId.value}`)
//     if (!res.ok) throw new Error('请求失败')
//     const data = await res.json()
//     knowledge.title = data.title
//     knowledge.description = data.description
//     knowledge.resources = data.resources
//     currentResourceId.value = data.resources.length ? data.resources[0].id : ''
//   } catch (e) {
//     ElMessage.error('加载知识点资源失败')
//   }
// }

// watch(currentResourceId, (newId) => {
//   currentResource.value = knowledge.resources.find(r => r.id === newId) || null
// })

async function fetchKnowledge() {
  try {
    // 模拟响应数据
    const data = {
      title: '卷积神经网络（CNN）',
      description: '本知识点介绍卷积神经网络的基础原理、应用和结构。',
      resources: [
        {
          id: 'r1',
          type: 'video',
          name: 'CNN 原理讲解',
          url: 'https://example.com/videos/cnn-intro.mp4', // 模拟视频链接
        },
        {
          id: 'r2',
          type: 'pdf',
          name: 'CNN 学习笔记',
          url: 'https://example.com/docs/cnn-notes.pdf',
        },
        {
          id: 'r3',
          type: 'text',
          name: 'CNN 概念简介',
          content: '卷积神经网络是一种深度学习模型，常用于图像识别与处理任务……',
        },
        {
          id: 'r4',
          type: 'ppt',
          name: 'CNN 课程讲义',
          url: 'https://example.com/ppt/cnn-slides.pptx',
        }
      ],
    }

    // 使用模拟数据赋值
    knowledge.title = data.title
    knowledge.description = data.description
    knowledge.resources = data.resources
    currentResourceId.value = data.resources.length ? data.resources[0].id : ''
  } catch (e) {
    ElMessage.error('加载知识点资源失败')
  }
}


onMounted(() => {
  fetchKnowledge()
})
</script>

<template>
  <div class="knowledge-study p-4 max-w-5xl mx-auto">
    <h2 class="text-2xl font-bold mb-2">{{ knowledge.title }}</h2>
    <p class="mb-6 text-gray-600">{{ knowledge.description }}</p>

    <el-tabs v-model="currentResourceId" type="border-card" stretch>
      <el-tab-pane
        v-for="res in knowledge.resources"
        :key="res.id"
        :label="res.name"
        :name="res.id"
      >
        <!-- 视频类型资源用 VideoPlay 组件 -->
        <VideoPlay
          v-if="res.type === 'video'"
          :url="res.url"
          poster=""
        />

        <!-- 其他资源展示同之前 -->
        <div v-else-if="res.type === 'pdf'">
          <p>文档文件: {{ res.name }}</p>
          <el-button
            type="primary"
            :href="res.url"
            target="_blank"
            icon="el-icon-download"
          >下载文档</el-button>
        </div>

        <div v-else-if="res.type === 'code'" style="background:#f5f5f5; padding:12px; border-radius:6px; white-space: pre-wrap; font-family: monospace;">
          <h4>示例代码:</h4>
          <code>{{ res.content }}</code>
        </div>

        <div v-else>
          <p>暂不支持的资源类型：{{ res.type }}</p>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
