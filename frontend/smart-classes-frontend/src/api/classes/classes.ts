export type Classes = {
  id: number
  name: string
  teacher_id: number
  credit: number
  class_hours: number
  graph: string
  active: boolean
  description: string
  image: string
}

export type CourseDisplayData = {
  name: string
  image: string
  description: string
  unfinished: number
  total: number
}
