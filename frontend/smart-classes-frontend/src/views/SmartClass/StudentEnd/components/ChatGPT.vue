<template>
  <div class="chat-wrapper">
    <!-- 聊天内容区域 -->
    <div class="chat-body">
      <div v-for="(msg, index) in messages" :key="index" class="chat-message" :class="msg.role">
        <div class="message-bubble">
          <span v-if="msg.role === 'user'" class="name"></span>
          <span v-else class="name"></span>
          <div class="content" v-html="renderMarkdown(msg.content)"></div>
        </div>
      </div>
    </div>

    <div class="chat-tools flex gap-2 mb-2">
      <ElButton
        v-for="item in quickActionsWithIcon"
        :key="item.label"
        @click="submitMessage"
        size="small"
        round
        class="quick-button"
        :icon="item.iconComponent"
      >
        {{ item.label }}
      </ElButton>
    </div>

    <div class="chat-input-area flex items-end gap-2">
      <el-input
        v-model="userInput"
        type="textarea"
        placeholder="请输入问题..."
        :autosize="{ minRows: 1, maxRows: 4 }"
        class="chat-textarea"
        @keyup.enter.exact="submitMessage"
        :disabled="generating"
      />
      <ElButton
        type="primary"
        :disabled="generating"
        class="send-button"
        @click="submitMessage"
        :icon="sendIcon"
        circle
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useIcon } from '@/hooks/web/useIcon'
import { ElButton } from 'element-plus'
import { fetchDifyAnswerStream } from '@/api/dify/index'
import { DifyChatRequest } from '@/api/dify/types'
import MarkdownIt from 'markdown-it'

const md = new MarkdownIt()

const renderMarkdown = (text: string) => {
  return md.render(text)
}
const userInput = ref('')
const messages = ref<{ role: 'user' | 'ai'; content: string }[]>([
  {
    role: 'ai',
    content: '我是智能AI助手，你可以向我提问你想问的问题，我会尽可能的给你帮助 😊'
  }
])
const generating = ref(false)
const conversationId = ref<string | null>(null)

// 流式响应处理
const fetchAnswer = async (question: string) => {
  messages.value.push({ role: 'user', content: question })
  const controller = new AbortController()
  let aiContent = ''
  messages.value.push({ role: 'ai', content: '' })
  generating.value = true
  try {
    const request: DifyChatRequest = {
      inputs: {},
      query: question,
      response_mode: 'streaming',
      user: 'test-user',
      auto_generate_name: false,
      files: [],
      ...(conversationId.value && { conversation_id: conversationId.value })
    }
    console.log('[fetchAnswer] request body:', request)
    await fetchDifyAnswerStream(
      request,
      (chunk) => {
        console.log('[stream chunk received]:', chunk)
        if (chunk.event === 'message') {
          if (chunk.conversation_id && !conversationId.value) {
            conversationId.value = chunk.conversation_id
            console.log('[conversationId set to]:', conversationId.value)
          }
          aiContent += chunk.answer || ''
          console.log('[AI message building up]:', aiContent)
          messages.value[messages.value.length - 1].content = aiContent
        }
      },
      controller.signal
    )
  } catch (e) {
    console.error('流式对话失败', e)
  } finally {
    generating.value = false
  }
  console.log('[messages list after AI reply]:', JSON.stringify(messages.value, null, 2))
}

const quickActions = [
  // { label: '路径推荐', text: '推荐学习路径', iconName: 'vi-ant-design:project-outlined' },
  // { label: '知识简介', text: '知识点简介', iconName: 'vi-ant-design:info-circle-outlined' },
  // {
  //   label: '资源清单',
  //   text: '我有哪些资源可以学习？',
  //   iconName: 'vi-ant-design:folder-open-outlined'
  // }
  { label: '你好', text: '你好', iconName: 'vi-ant-design:info-circle-outlined' }
]

const quickActionsWithIcon = quickActions.map((item) => ({
  ...item,
  iconComponent: useIcon({ icon: item.iconName })
}))

const sendIcon = useIcon({ icon: 'vi-ant-design:send-outlined' })

const submitMessage = () => {
  const question = userInput.value.trim()
  console.log(question)
  if (!question) return
  fetchAnswer(question)
  userInput.value = ''
}
</script>

<style scoped>
.chat-wrapper {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 12px;
  box-sizing: border-box;
  background: #f5f7fa;
}

/* 聊天区域 */
.chat-body {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
  height: 100%;
  border-radius: 12px;
  background: white;
  margin-bottom: 12px;
  border: 1px solid #e5e7eb;
}

/* 每条消息 */
.chat-message {
  margin-bottom: 12px;
  display: flex;
  flex-direction: column;
}
.chat-message.user .message-bubble {
  align-self: flex-end;
  background-color: #e0f3ff;
}
.chat-message.ai .message-bubble {
  align-self: flex-start;
  background-color: #f0f0f0;
}

.message-bubble {
  max-width: 80%;
  padding: 10px 14px;
  border-radius: 12px;
  line-height: 1.5;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: background-color 0.3s;
}
.name {
  font-weight: bold;
  margin-right: 6px;
}
.content {
  white-space: pre-wrap;
  word-break: break-word;
  margin-top: 4px;
  margin-left: 4px;
}

/* 快捷按钮样式 */
.chat-tools {
  padding: 0.2rem;
  background-color: #f9fafb;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
}
.quick-button {
  border-radius: 20px;
  background-color: #fff;
  color: #333;
}
.quick-button:hover {
  background-color: #f0f0f0;
}

/* 输入区 */
.chat-input-area {
  padding: 0.5rem;
  background-color: #ffffff;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
}
.chat-textarea :deep(.el-textarea__inner) {
  border: none;
  border-radius: 12px;
  background-color: #ffffff;
  padding: 8px 12px;
  font-size: 14px;
  color: #333;
  transition: background-color 0.2s;
}
.chat-textarea :deep(.el-textarea__inner:focus) {
  background-color: #f0f0f0;
}

/* 发送按钮 */
.send-button {
  background-color: #000000;
  color: white;
  width: 40px;
  height: 40px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.send-button:hover {
  background-color: #222;
}
</style>
