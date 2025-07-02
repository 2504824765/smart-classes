import { Student } from '@/api/student/types'
import { ClassMission } from '@/api/classMission/types'

export type StudentMission = {
  id: number
  student: Student
  classMission: ClassMission
  score: number
  done: boolean
  active: boolean
}

export type StudentMissionCreateDTO = Omit<StudentMission, 'id'>
