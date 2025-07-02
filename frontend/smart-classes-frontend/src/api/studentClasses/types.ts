export type StudentClasses = {
  id: number
  sid: number
  cid: number
  grade: number
}

export type StudentClassesCreateDTO = Omit<StudentClasses, 'id'>

export type StudentClassesUpdateDTO = Partial<Omit<StudentClasses, 'id'>> & {
  id: number
}
