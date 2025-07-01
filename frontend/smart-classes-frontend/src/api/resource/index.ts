import request from '@/axios'
import { Resource, ResourceCreateDTO } from './types'

export const getResourceListApi = (params: any) => {
  return request.get({ url: '/api/resource/all', params })
}

// 新增资源
export const addResourceApi = (data: ResourceCreateDTO) => {
  return request.post({ url: '/api/resource/add', data })
}

// 删除资源
export const deleteResourceApi = (id: number) => {
  return request.delete({ url: `/api/resource/delete/${id}` })
}

// 更新资源
export const updateResourceApi = (data: Resource) => {
  return request.post({ url: '/api/resource/update', data })
}

// 获取所有资源
export const getAllResourceApi = () => {
  return request.get({ url: '/api/resource/all' })
}

// 根据id获取资源
export const getResourceByIdApi = (id: number) => {
  return request.get({ url: `/api/resource/${id}` })
}

// 根据类型获取资源
export const getResourceByTypeApi = (type: string) => {
  return request.get({ url: `/api/resource/type/${type}` })
}
