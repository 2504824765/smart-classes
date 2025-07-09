import request from '@/axios'
import { StudentMission, StudentMissionCreateDTO } from './types'
import { Student } from '../student/types'

export const getStudentMissionByClass = async (
  classId: number,
  studentId: number
): Promise<IResponse<StudentMission[]>> => {
  return request.post({
    url: '/api/studentMission/getStudentsAllClassMission',
    data: { classId, studentId }
  })
}

export const getStudentMissionByStudent = async (
  studentId: number
): Promise<IResponse<StudentMission[]>> => {
  return request.get({ url: '/api/studentMission/student', params: { studentId } })
}

export const getStudentMissionByMission = async (
  missionId: number
): Promise<IResponse<StudentMission[]>> => {
  return request.get({ url: `/api/allStudentsOfClassMission/${missionId}` })
}

export const getStudentMissionById = async (id: number): Promise<IResponse<StudentMission>> => {
  return request.get({ url: `/api/studentMission/getStudentMissionById/${id}` })
}

export const addStudentMission = async (
  data: StudentMissionCreateDTO
): Promise<IResponse<StudentMission>> => {
  return request.post({ url: '/api/studentMission/add', data })
}

export const updateStudentMission = async (data: StudentMission) => {
  return request.put({ url: `/api/studentMission/update`, data })
}

export const deleteStudentMission = async (id: number): Promise<IResponse<boolean>> => {
  return request.delete({ url: `/api/studentMission/delete/${id}` })
}

export const getStudentMissionByStudentIdAndClassMissionId = async (
  studentId: number,
  classMissionId: number
): Promise<IResponse<StudentMission>> => {
  return request.get({
    url: `/api/studentMission/getStudentMissionByStudentIdAndClassMissionId/`,
    data: { studentId, classMissionId }
  })
}
