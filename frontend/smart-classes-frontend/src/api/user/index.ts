import request from '@/axios'
import type { User } from './types'

// 获取所有用户
export const getAllUserApi = (): Promise<IResponse<User[]>> => {
  return request.get({ url: '/api/user/all' })
}

// 根据用户名获取用户
export const getUserByUsernameApi = (username: string): Promise<IResponse<User>> => {
  return request.get({ url: `/api/user/getUserByUsername/${username}` })
}

// 根据ID获取用户
export const getUserByIdApi = (id: number): Promise<IResponse<User>> => {
  return request.get({ url: `/api/user/getUserById/${id}` })
}

// 删除用户
export const deleteUserApi = (id: number): Promise<IResponse<boolean>> => {
  return request.delete({ url: `/api/user/deleteUser/${id}` })
} 