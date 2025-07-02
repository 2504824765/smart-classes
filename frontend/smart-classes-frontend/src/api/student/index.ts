import request from '@/axios'
import type { Student, StudentCreateDTO, StudentUpdateDTO } from './types'

export const getStudentListApi = (params: any) => {
  return request.get({ url: '/api/student/all', params })
}

// 根据ID获取学生
export const getStudentByIdApi = (id: number | string): Promise<IResponse<Student>> => {
  return request.get({ url: `/api/student/${id}` })
}

// 更新学生信息
export const updateStudentApi = (data: StudentUpdateDTO): Promise<IResponse<Student>> => {
  return request.post({ url: '/api/student/update', data })
}

// 根据用户名获取学生
export const getStudentByUsernameApi = (username: string): Promise<IResponse<Student>> => {
  return request.get({ url: `/api/student/getStudentByUsername/${username}` })
}

// 获取所有学生
export const getAllStudentApi = (): Promise<IResponse<Student[]>> => {
  return request.get({ url: '/api/student/all' })
}

// 新增学生
export const createStudentApi = (data: StudentCreateDTO): Promise<IResponse<Student>> => {
  return request.post({ url: '/api/student/add', data })
}

// 删除学生
export const deleteStudentApi = (id: number | string) => {
  return request.delete({ url: `/api/student/${id}` })
}

export const getStudentCountApi = () => {
  return request.get({ url: '/api/student/count' })
}
