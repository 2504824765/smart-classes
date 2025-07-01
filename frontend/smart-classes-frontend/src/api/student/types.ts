import { Department } from "../department/types"

export type Student = {
  id: number
  name: string
  gender: string
  // dept: string
  dept: Department
  gpa: number
}

export type StudentCreateDTO = Omit<Student, 'id' | 'dept'> & {
  dept_id: number
}

export type StudentUpdateDTO = Partial<{
  id: number
} & Omit<Student, 'id' | 'dept'>> & {
  id: number
  dept_id: number
}