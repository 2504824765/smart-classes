import { Student } from '@/api/student/types'
import { ClassMission } from '@/api/classMission/types'

export type StudentMission = {
  id: number
  student: Student
  classMission: ClassMission
  score: number
  isDone: boolean
  isActive: boolean
  reportUrl: string
}

export type StudentMissionCreateDTO = Omit<StudentMission, 'id' | 'student' | 'classMission'> & {
  studentId: number
  classMissionId: number
}
