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
 * @param user 用户标识（可选，默认值 "test-user"）
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
