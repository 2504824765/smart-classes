export type Resource = {
  id: number
  name: string
  path: string
  type: string
  description: string
  classId: number
}

export type ResourceCreateDTO = Omit<Resource, 'id'>