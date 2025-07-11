import { Department } from '../department/types'

export type Teacher = {
  id: number
  username: string
  name: string
  gender: string
  // dept: string
  department: Department
}

export type TeacherCreateDTO = Omit<Teacher, 'id' | 'department'> & {
  departmentId: number
}

export type TeacherUpdateDTO = Partial<
  {
    id: number
  } & Omit<Teacher, 'id' | 'department'>
> & {
  id: number
  departmentId: number
}
