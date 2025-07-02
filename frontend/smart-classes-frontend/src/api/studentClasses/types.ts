import { Student } from '../student/types'
import { Classes } from '../classes/types'

export type StudentClasses = {
  id: number
  // sid: number
  // cid: number
  student: Student
  classes: Classes
  grade: number
}

export type StudentClassesCreateDTO = Omit<StudentClasses, 'id'>

export type StudentClassesUpdateDTO = Partial<Omit<StudentClasses, 'id'>> & {
  id: number
}
