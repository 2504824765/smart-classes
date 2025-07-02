import request from '@/axios'
import type { ClassesCreateDTO, ClassesUpdateDTO, Classes } from './types'

export const getClassListApi = (params: any) => {
  return request.get({ url: '/api/class/all', params })
}

// 新增课程
export const addClassApi = (data: ClassesCreateDTO): Promise<IResponse<Classes>> => {
  return request.post({ url: '/api/class/add', data })
}

// 删除课程
export const deleteClassApi = (id: number) => {
  return request.delete({ url: `/api/class/delete/${id}` })
}

// 更新课程
export const updateClassApi = (data: ClassesUpdateDTO):Promise<IResponse<Classes>> => {
  return request.post({ url: '/api/class/update', data })
}

// 获取所有课程
export const getAllClassesApi = (): Promise<IResponse<Classes[]>> => {
  return request.get({ url: '/api/class/all' })
}

// 根据课程id获取课程信息
export const getClassesByIdApi = (id: number): Promise<IResponse<Classes>> => {
  return request.get({ url: `/api/class/getClassById/${id}` })
}

// 根据课程名称获取课程信息
export const getClassesByNameApi = (name: string): Promise<IResponse<Classes>> =>{
  return request.get({ url: `/api/class/getClassByName/${name}` })
}

// 根据教师id获取课程信息
export const getClassesByTeacherApi = (teacher_id: number): Promise<IResponse<Classes[]>> => {
  return request.get({ url: `/api/class/getClassByTeacher/${teacher_id}` })
}

// 获取课程数量
export const getClassCountApi = ():Promise<IResponse<number>> => {
  return request.get({ url: '/api/class/count' })
}
