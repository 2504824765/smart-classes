import { Classes } from '../classes/types'

export type ClassMission = {
  id: number
  classes: Classes
  type: string
  description: string
  deadline: string
  submitMethod: string
  score: number
  resource: number
}

export type ClassMissionCreateDTO = {
  cid: number
} & Omit<ClassMission, 'id' | 'class'> // 排除id和class

export type ClassMissionUpdateDTO = Partial<{
  cid: number
} & Omit<ClassMission, 'id' | 'class'>> & {
  id: number // 必须包含ID
}