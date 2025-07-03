import { Department } from '../department/types'

export type Student = {
  id: number
  name: string
  gender: string
  username: string
  department: Department
  gpa: number
}

export type StudentCreateDTO = Omit<Student, 'id' | 'department'> & {
  deptId: number
}

export type StudentUpdateDTO = Partial<
  {
    id: number
  } & Omit<Student, 'id' | 'department'>
> & {
  id: number
  deptId: number
}
