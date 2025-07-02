// import request from '@/axios'
// import { DepartmentListResponse, DepartmentUserParams, DepartmentUserResponse } from './types'

// export const getDepartmentApi = () => {
//   return request.get<DepartmentListResponse>({ url: '/mock/department/list' })
// }

// export const getUserByIdApi = (params: DepartmentUserParams) => {
//   return request.get<DepartmentUserResponse>({ url: '/mock/department/users', params })
// }

// export const deleteUserByIdApi = (ids: string[] | number[]) => {
//   return request.post({ url: '/mock/department/user/delete', data: { ids } })
// }

// export const saveUserApi = (data: any) => {
//   return request.post({ url: '/mock/department/user/save', data })
// }

// export const saveDepartmentApi = (data: any) => {
//   return request.post({ url: '/mock/department/save', data })
// }

// export const deleteDepartmentApi = (ids: string[] | number[]) => {
//   return request.post({ url: '/mock/department/delete', data: { ids } })
// }

// export const getDepartmentTableApi = (params: any) => {
//   return request.get({ url: '/mock/department/table/list', params })
// }

import request from '@/axios'
import type { Department, DepartmentCreateDTO, DepartmentUpdateDTO } from './types'

export const getDepartmentListApi = (params: any) => {
  return request.get({ url: '/api/dept/all', params })
}

// 新增组织
export const addDepartmentApi = (data: DepartmentCreateDTO): Promise<IResponse<Department>> => {
  return request.post({ url: '/api/dept/add', data })
}

// 删除组织
export const deleteDepartmentApi = (id: number) => {
  return request.delete({ url: `/api/dept/${id}` })
}

// 更新组织
export const updateDepartmentApi = (data: DepartmentUpdateDTO): Promise<IResponse<Department>> => {
  return request.post({ url: '/api/dept/update', data })
}

// 获取所有组织
export const getAllDeptApi = (): Promise<IResponse<Department[]>> => {
  return request.get({ url: '/api/dept/all' })
}

// 根据组织ID获取组织
export const getDeptByIdApi = (id: number): Promise<IResponse<Department>> => {
  return request.get({ url: `/api/dept/getDeptById/${id}` })
}

// 根据组织名称获取组织
export const getDeptByNameApi = (name: string): Promise<IResponse<Department>> => {
  return request.get({ url: `/api/dept/getDeptByName/${name}` })
}


export const getMembersByDeptIdApi = (id: number): Promise<IResponse<any>> => {
  return request.get({ url: `/api/dept/getMembersByDeptId/${id}` })
}