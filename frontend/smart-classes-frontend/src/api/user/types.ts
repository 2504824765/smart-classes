export type User = {
  id: number
  username: string
  password: string
  role: string
  imageURL?: string

}

export type UserCreateDTO = Omit<User, 'id'>

export type UserUpdateDTO = Partial<Omit<User, 'id'>> & {
  id: number

} 