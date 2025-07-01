export type Teacher = {
  id: number
  username: string
  name: string
  gender: string
  dept: string
}

export type TeacherCreateDTO = Omit<Teacher, 'id'>
