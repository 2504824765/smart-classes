import { Department } from '../department/types'

export type Student = {
  id: number
  name: string
  gender: string
  username: string
  department: Department
  gpa: number
}

export type StudentCreateDTO = Omit<Student, 'id' | 'dept'> & {
  deptId: number
}

export type StudentUpdateDTO = Partial<
  {
    id: number
  } & Omit<Student, 'id' | 'dept'>
> & {
  id: number
  deptId: number
}
