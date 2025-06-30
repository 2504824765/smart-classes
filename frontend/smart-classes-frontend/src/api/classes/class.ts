import { Teacher } from '../teacher/teacher'

export type Classes = {
  id: number
  name: string
  teacher: Teacher
  credit: number
  classHours: number
  graph: string
}
