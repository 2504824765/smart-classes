export interface UserLoginType {
  username: string
  password: string
  role: string
}

export interface UserType {
  username: string
  password: string
  role: string
  roleId: number
}

export interface UserBackEnd {
  id: number
  username: string
  role: string
  imageURL: string
}
