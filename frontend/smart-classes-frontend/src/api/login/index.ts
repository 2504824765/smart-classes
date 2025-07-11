import request from '@/axios'
import type { UserType, UserBackEnd } from './types'
import axios from '@/axios'

interface RoleParams {
  roleName: string
}

export const loginApi = (data: UserType): Promise<IResponse<boolean>> => {
  return request.post({ url: '/api/user/login', data })
}

export const loginOutApi = (): Promise<IResponse> => {
  return request.get({ url: '/api/user/loginOut' })
}

export const registerApi = (data: UserType): Promise<IResponse<boolean>> => {
  return request.post({ url: '/api/user/register', data })
}

export const getUserInfoApi = (username: string): Promise<IResponse<UserBackEnd>> => {
  return request.get({ url: `/api/user/getUserByUsername/${username}` })
}

// 根据ID获取用户信息
export const getUserByIdApi = (id: number): Promise<IResponse<UserBackEnd>> => {
  return request.get({ url: `/api/user/getUserById/${id}` })
}

export const getUserListApi = ({ params }: AxiosConfig) => {
  return request.get<{
    code: string
    data: {
      list: UserType[]
      total: number
    }
  }>({ url: '/mock/user/list', params })
}

export const getAdminRoleApi = (
  params: RoleParams
): Promise<IResponse<AppCustomRouteRecordRaw[]>> => {
  return request.get({ url: '/mock/role/list', params })
}

export const getTestRoleApi = (params: RoleParams): Promise<IResponse<string[]>> => {
  return request.get({ url: '/mock/role/list2', params })
}
