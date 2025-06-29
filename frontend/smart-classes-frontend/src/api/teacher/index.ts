import request from '@/axios'
import type { Teacher } from './teacher'

export const getTeacherListApi = (params: any) => {
  return request.get({ url: '/api/teacher/all', params })
}

// 根据ID获取教师
export const getTeacherByIdApi = (id: number) => {
  return request.get({ url: `/api/teacher/${id}` })
}

// 更新教师信息
export const updateTeacherApi = (data: Teacher) => {
  return request.post({ url: '/api/teacher/update', data })
}

// 根据用户名获取教师
export const getTeacherByUsernameApi = (username: string) => {
  return request.get({ url: `/api/teacher/getTeacherByUsername/${username}` })
}

// 获取所有教师
export const getAllTeacherApi = () => {
  return request.get({ url: '/api/teacher/all' })
}

// 新增教师
export const createTeacherApi = (data: Teacher) => {
  return request.post({ url: '/api/teacher/add', data })
}

// 删除教师
export const deleteTeacherApi = (id: number) => {
  return request.delete({ url: `/api/teacher/${id}` })
}
