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
      Authorization: `Bearer app-en9wHAJwHtXbetwFIp5G5aSK` // TODO: 替换为真实 key
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

    const parts = buffer.split('\n')
    for (let i = 0; i < parts.length - 1; i++) {
      const line = parts[i].trim()
      if (line.startsWith('{')) {
        try {
          const json = JSON.parse(line)
          onMessage(json)
        } catch (err) {
          console.warn('JSON parse error:', err, line)
        }
      }
    }
    buffer = parts[parts.length - 1]
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
