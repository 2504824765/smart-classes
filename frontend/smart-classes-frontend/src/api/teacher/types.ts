import { Department } from "../department/types"

export type Teacher = {
  id: number
  username: string
  name: string
  gender: string
  // dept: string
  dept: Department
}

export type TeacherCreateDTO = Omit<Teacher, 'id' | 'dept'> & {
  deptId: number
}

export type TeacherUpdateDTO = Partial<{
  id: number
} & Omit<Teacher, 'id' | 'dept'>> & {
  id: number
  deptId: number
}