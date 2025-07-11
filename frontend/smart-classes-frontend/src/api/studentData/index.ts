import request from '@/axios'
import type { StudentData, StudentDataCreateDTO, StudentDataUpdateDTO } from './types'

// 新增学生数据
export const addStudentDataApi = (data: StudentDataCreateDTO): Promise<IResponse<StudentData>> => {
  return request.post({ url: '/api/studentData/add', data })
}

// 删除学生数据
export const deleteStudentDataApi = (id: number) => {
  return request.delete({ url: `/api/studentData/${id}` })
}

// 更新学生数据信息
export const updateStudentDataApi = (
  data: StudentDataUpdateDTO
): Promise<IResponse<StudentData>> => {
  return request.post({ url: '/api/studentData/update', data })
}

// 根据ID获取学生数据
export const getStudentDataByIdApi = (id: number): Promise<IResponse<StudentData>> => {
  return request.get({ url: `/api/studentData/getStudentDataById/${id}` })
}
