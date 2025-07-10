import { Teacher } from '../teacher/types'

export type Classes = {
  id: number
  name: string
  teacher: Teacher
  credit: number
  classHours: number
  graph: string
  isActive: boolean
  description: string
  imageUrl: string
}

export type CourseDisplayData = {
  id: number
  name: string
  imageUrl: string
  description: string
  unfinished: number
  total: number
  isActive: boolean
}

// 创建课程时，只需要teacherId，不需要完整的teacher对象
export type ClassesCreateDTO = {
  name: string
  teacherId: number
  credit: number
  classHours: number
  graph: string
  isActive: boolean
  description: string
  imageUrl: string
}

// 更新课程时，需要id和teacherId
export type ClassesUpdateDTO = {
  id: number
  name: string
  teacherId: number
  credit: number
  classHours: number
  graph: string
  isActive: boolean
  description: string
  imageUrl: string
}
