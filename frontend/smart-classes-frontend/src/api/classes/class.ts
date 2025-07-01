import { Teacher } from '../teacher/types'

export type Classes = {
  id: number
  name: string
  teacher: Teacher
  credit: number
  classHours: number
  graph: string
}
