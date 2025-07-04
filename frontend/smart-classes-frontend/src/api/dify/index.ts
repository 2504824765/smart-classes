import request from '@/axios'
import type { DifyGraphRequest, DifyGenerateQuestionRequest, DifyChatRequest } from './types'

const DIFY_CHAT_URL = 'https://api.dify.ai/v1/chat-messages'

/**
 * 向 Dify 发送流式对话请求
 * @param body 请求体（符合 DifyChatRequest 类型）
 * @param onMessage 回调函数处理每个 JSON 块
 * @param signal AbortSignal 用于取消请求
 */
export async function fetchDifyAnswerStream(
  body: DifyChatRequest,
  onMessage: (data: {
    answer?: string
    conversation_id?: string
    message_id?: string
    created_at?: number
    [key: string]: any
  }) => void,
  signal?: AbortSignal
): Promise<void> {
  const res = await fetch(DIFY_CHAT_URL, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer app-en9wHAJwHtXbetwFIp5G5aSK`
    },
    body: JSON.stringify(body),
    signal
  })

  if (!res.body) throw new Error('Empty response body')

  const reader = res.body.getReader()
  const decoder = new TextDecoder('utf-8')
  let buffer = ''

  while (true) {
    const { value, done } = await reader.read()
    if (done) break
    buffer += decoder.decode(value, { stream: true })

    const lines = buffer.split('\n').filter(line => line.trim())
    for (const line of lines) {
      if (line.trim() === 'data: [DONE]') {
        return
      }

      if (line.startsWith('data:')) {
        const jsonStr = line.replace(/^data:\s*/, '')
        try {
          const data = JSON.parse(jsonStr)
          onMessage(data)
        } catch (err) {
          console.warn('JSON parse error:', err, jsonStr)
        }
      }
    }

    buffer = ''
  }
}



export const createGraphApi = (data: DifyGraphRequest): Promise<IResponse<string>> => {
  return request.post({ url: '/api/dify/createGraph', data })
}

export const generateQuestionApi = (
  data: DifyGenerateQuestionRequest
): Promise<IResponse<string>> => {
  return request.post({ url: '/api/dify/generateQuestion', data })
}
