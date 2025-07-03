import { Classes } from "../classes/types"
import { Student } from "../student/types"

export type StudentClasses = {
  id: number
  classes: Classes
  student: Student
  grade: number
}

export type StudentClassesCreateDTO = Omit<StudentClasses, 'id'>

export type StudentClassesUpdateDTO = Partial<Omit<StudentClasses, 'id'>> & {
  id: number
}
