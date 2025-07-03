import { Department } from '../department/types'
import { StudentData } from '../studentData/types'

export type Student = {
  id: number
  name: string
  gender: string
  username: string
  department: Department
  gpa: number
  studentData: StudentData
}

export type StudentCreateDTO = Omit<Student, 'id' | 'dept' | 'studentData'> & {
  deptId: number
  studentDataId: number
}

export type StudentUpdateDTO = Partial<
  {
    id: number
  } & Omit<Student, 'id' | 'dept' | 'studentData'>
> & {
  id: number
  deptId: number
  studentDataId: number
}
