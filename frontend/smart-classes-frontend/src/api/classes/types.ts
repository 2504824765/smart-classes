export type Classes = {
  id: number
  name: string
  teacherId: number
  credit: number
  classHours: number
  graph: string
  active: boolean
  description: string
  imageUrl: string
}

export type CourseDisplayData = {
  name: string
  imageUrl: string
  description: string
  unfinished: number
  total: number
}

export type ClassesCreateDTO = Omit<Classes, 'id'>

export type ClassesUpdateDTO = Partial<Omit<Classes, 'id'>> & {
  id: number // 更新时必须包含id
}