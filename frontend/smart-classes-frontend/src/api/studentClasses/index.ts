import request from '@/axios'
import { StudentClasses, StudentClassesCreateDTO, StudentClassesUpdateDTO } from './types'

export const getStudentClassesListApi = (params: any) => {
  return request.get({ url: '/api/scAssociated/all', params })
}

// 新增学生课程关联记录
export const addClassRecordApi = (
  data: StudentClassesCreateDTO
): Promise<IResponse<StudentClasses>> => {
  return request.post({ url: '/api/scAssociated/add', data })
}

// 根据id删除学生课程关联记录
export const deleteClassRecordByIdApi = (id: number) => {
  return request.delete({ url: `/api/scAssociated/delete/${id}` })
}

// 更新学生课程关联记录
export const updateClassRecordApi = (
  data: StudentClassesUpdateDTO
): Promise<IResponse<StudentClasses>> => {
  return request.post({ url: '/api/scAssociated/update', data })
}

// 根据id获取学生课程关联记录
export const getStudentClassesByIdApi = (id: number): Promise<IResponse<StudentClasses>> => {
  return request.get({ url: `/api/scAssociated/getAssociatedById/${id}` })
}

// 根据学生id获取学生课程关联记录
export const getAssociatedBySidApi = (sid: number): Promise<IResponse<StudentClasses[]>> => {
  return request.get({ url: `/api/scAssociated/getAssociatedBySid/${sid}` })
}

// 根据课程id获取学生课程关联记录
export const getAssociatedByCidApi = (cid: number): Promise<IResponse<StudentClasses[]>> => {
  return request.get({ url: `/api/scAssociated/getAssociatedByCid/${cid}` })
}
