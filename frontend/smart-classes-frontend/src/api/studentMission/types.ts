import { Student } from '@/api/student/types'
import { ClassMission } from '@/api/classMission/types'
import { Resource } from '@/api/resource/types'

export type StudentMission = {
  id: number
  student: Student
  classMission: ClassMission
  score: number
  done: boolean
  active: boolean
  resource: Resource
}

export type StudentMissionCreateDTO = Omit<StudentMission, 'id'>
