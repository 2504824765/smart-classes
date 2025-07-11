import { Classes } from '../classes/types'
import { Resource } from '../resource/types'

export type ClassMission = {
  id: number
  classes: Classes
  type: string
  description: string
  deadline: string
  submitMethod: string
  score: number
  resource: Resource
}

export type ClassMissionCreateDTO = {
  cid: number
  resource: number
} & Omit<ClassMission, 'id' | 'class' | 'resource'> // 排除id和class

export type ClassMissionUpdateDTO = Partial<
  {
    cid: number
  } & Omit<ClassMission, 'id' | 'class'>
> & {
  id: number // 必须包含ID
}
