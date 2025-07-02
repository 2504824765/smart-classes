import request from '@/axios'
import { ClassMissionCreateDTO, ClassMissionUpdateDTO, ClassMission } from './types'

export const getClassMissionApi = (params: any) => {
  return request.get({ url: '/api/classMission/all', params })
}

// 新增课程任务
export const addClassMissionApi = (data: ClassMissionCreateDTO): Promise<IResponse<ClassMission>> => {
  return request.post({ url: '/api/classMission/add', data })
}

// 删除课程任务
export const deleteClassMissionApi = (id: number) => {
  return request.delete({ url: `/api/classMission/delete/${id}` })
}

// 更新课程任务
export const updateClassMissionApi = (data: ClassMissionUpdateDTO): Promise<IResponse<ClassMission>> => {
  return request.post({ url: '/api/classMission/update', data })
}

// 获取所有课程任务
export const getallClassMissionApi = (): Promise<IResponse<ClassMission[]>> => {
  return request.get({ url: '/api/classMission/all' })
}

// 根据课程id获取课程任务
export const getClassMissionByCidApi = (cid: number): Promise<IResponse<ClassMission[]>> => {
  return request.get({ url: `/api/classMission/getClassMissionByCid/${cid}` })
}
