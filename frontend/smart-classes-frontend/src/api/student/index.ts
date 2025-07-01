import request from '@/axios'
import type { Student } from './types'

export const getStudentListApi = (params: any) => {
  return request.get({ url: '/api/student/all', params })
}

// 根据ID获取学生
export const getStudentByIdApi = (id: number | string) => {
  return request.get({ url: `/api/student/${id}` })
}

// 更新学生信息
export const updateStudentApi = (data: any) => {
  return request.post({ url: '/api/student/update', data })
}

// 根据用户名获取学生
export const getStudentByUsernameApi = (username: string) => {
  return request.get({ url: `/api/student/getStudentByUsername/${username}` })
}

// 获取所有学生
export const getAllStudentApi = () => {
  return request.get({ url: '/api/student/all' })
}

// 新增学生
export const createStudentApi = (data: any) => {
  return request.post({ url: '/api/student/add', data })
}

// 删除学生
export const deleteStudentApi = (id: number | string) => {
  return request.delete({ url: `/api/student/${id}` })
}
