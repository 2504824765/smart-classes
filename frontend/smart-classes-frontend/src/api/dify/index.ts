import request from '@/axios'
import type { DifyGraphRequest, DifyGenerateQuestionRequest } from './types'

export const createGraphApi = (data: DifyGraphRequest): Promise<IResponse<string>> => {
  return request.post({ url: '/api/dify/createGraph', data })
}

export const generateQuestionApi = (
  data: DifyGenerateQuestionRequest
): Promise<IResponse<string>> => {
  return request.post({ url: '/api/dify/generateQuestion', data })
}
