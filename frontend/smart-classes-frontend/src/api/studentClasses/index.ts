import request from '@/axios'
import { StudentClasses } from './studentClasses'

// export const getStudentClassesListApi = (params: any) => {
//   return request.get({ url: '/api/scAssociated/all', params })
// }

// 新增学生课程关联记录
export const addClassRecordApi = (data: StudentClasses) => {
  return request.post({ url: '/api/scAssociated/add', data })
}

// 根据id删除学生课程关联记录
export const deleteClassRecordByIdApi = (id: number) => {
  return request.delete({ url: `/api/scAssociated/delete/${id}` })
}

// 更新学生课程关联记录
export const updateClassRecordApi = (data: StudentClasses) => {
  return request.post({ url: '/api/scAssociated/update', data })
}

// 根据id获取学生课程关联记录
export const getStudentClassesByIdApi = (id: number) => {
  return request.get({ url: `/api/scAssociated/getAssociatedById/${id}` })
}

// 根据学生id获取学生课程关联记录
export const getAssociatedBySidApi = (sid: number) => {
  return request.get({ url: `/api/scAssociated/getAssociatedBySid/${sid}` })
}

// 根据课程id获取学生课程关联记录
export const getAssociatedByCidApi = (cid: number) => {
  return request.get({ url: `/api/scAssociated/getAssociatedByCid/${cid}` })
}