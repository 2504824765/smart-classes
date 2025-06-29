import request from '@/axios'
import type { UserType } from './types'
import axios from '@/axios'

interface RoleParams {
  roleName: string
}

export const loginBackEnd = (data: UserType): Promise<IResponse<Boolean>> => {
  return request.post({ url: '/api/user/login', data })
}

// 测试用的登录接口，当后端不可用时使用
export const loginTest = (data: UserType): Promise<IResponse<Boolean>> => {
  // 模拟登录验证
  return new Promise((resolve) => {
    setTimeout(() => {
      // 简单的用户名密码验证
      if ((data.username === 'teacher' && data.password === 'teacher' && data.role === 'teacher') ||
          (data.username === 'student' && data.password === 'student' && data.role === 'student')) {
        resolve({
          code: 200,
          data: true
        })
      } else {
        resolve({
          code: 500,
          data: false
        })
      }
    }, 500)
  })
}

export const loginApi = (data: UserType): Promise<IResponse<UserType>> => {
  return request.post({ url: '/mock/user/login', data })
}

export const loginOutApi = (): Promise<IResponse> => {
  return request.get({ url: '/mock/user/loginOut' })
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
