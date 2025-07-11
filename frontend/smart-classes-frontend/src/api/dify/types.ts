export type DifyGraphRequest = {
  inputs: {
    file: {
      type: 'document'
      transfer_method: 'remote_url'
      url: string
    }[]
  }
  response_mode: 'streaming'
  user: string
}

export type DifyGenerateQuestionRequest = DifyGraphRequest & {
  inputs: DifyGraphRequest['inputs'] & {
    nodeName: string
    quantity: number
  }
}
/**
 * 构建 DifyGraphRequest 请求体
 * @param url 文件的远程 URL
 * @param user 用户标识（可选，默认值 'test-user'）
 */
export function createDifyGraphRequest(url: string, user = 'test-user'): DifyGraphRequest {
  return {
    inputs: {
      file: [
        {
          type: 'document',
          transfer_method: 'remote_url',
          url
        }
      ]
    },
    response_mode: 'streaming',
    user
  }
}

export function createDifyGraphRequestMulti(urls: string[], user = 'test-user'): DifyGraphRequest {
  return {
    inputs: {
      file: urls.map((url) => ({
        type: 'document',
        transfer_method: 'remote_url',
        url
      }))
    },
    response_mode: 'streaming',
    user
  }
}

export function createDifyGenerateQuestionRequest(
  urls: string[],
  nodeName: string,
  quantity: number,
  user = 'test-user'
): DifyGenerateQuestionRequest {
  return {
    inputs: {
      file: urls.map((url) => ({
        type: 'document',
        transfer_method: 'remote_url',
        url
      })),
      nodeName,
      quantity
    },
    response_mode: 'streaming',
    user
  }
}

// Dify 对话请求类型
export type DifyChatRequest = {
  inputs: Record<string, unknown> // 写死为空对象
  query: string // 用户输入的问题
  response_mode: 'streaming' // 写死
  user: string // 用户标识
  conversation_id?: string // 第一次对话不传
  files: unknown[] // 写死为空数组
  auto_generate_name: boolean // 写死 false
}

/**
 * 构建 DifyChatRequest 请求体
 * @param query 用户提问
 * @param conversationId 可选，对话上下文 ID（首次不传）
 * @param user 用户标识，默认 'test-user'
 */
export function createDifyChatRequest(
  query: string,
  conversationId?: string,
  user = 'test-user'
): DifyChatRequest {
  const request: DifyChatRequest = {
    inputs: {},
    query,
    response_mode: 'streaming',
    user,
    files: [],
    auto_generate_name: false
  }
  if (conversationId) {
    request.conversation_id = conversationId
  }
  return request
}

// Dify 流式响应的单条消息结构
export type DifyChatStreamResponse = {
  event: string
  conversation_id: string
  message_id: string
  created_at: number
  task_id: string
  id: string
  answer: string
  from_variable_selector: string[]
}

/**
 * 判断是否是有效的 Dify 响应 JSON
 */
export function isDifyChatStreamResponse(obj: any): obj is DifyChatStreamResponse {
  return (
    obj &&
    typeof obj === 'object' &&
    typeof obj.answer === 'string' &&
    typeof obj.conversation_id === 'string'
  )
}


/**
 * 批改报告 Dify 请求体
 */
export type DifyReportCommentRequest = {
inputs: {
    studentReport: 
    {
      type: 'document'
      transfer_method: 'remote_url'
      url: string
    },
    moBan: 
    {
      type: 'document'
      transfer_method: 'remote_url'
      url: string
    }
  },
  response_mode: 'streaming'
  user: 'test-user'
}

/**
 * 构建 DifyReportCommentRequest 请求体
 * @param urlReport 
 * @param urlMoban 
 * @returns 
 */
export function createDifyReportCommentRequest(urlReport: string, urlMoban: string): DifyReportCommentRequest {
  return {
    inputs: {
      studentReport: {
        type: 'document',
        transfer_method: 'remote_url',
        url: urlReport 
      },
      moBan: {
        type: 'document',
        transfer_method: 'remote_url',
        url: urlMoban 
      }
    },
    response_mode: 'streaming',
    user: 'test-user'
  }
}