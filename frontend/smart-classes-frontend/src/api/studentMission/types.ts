import { Student } from "@/api/student/types"
import { ClassMission } from "@/api/classMission/types"

export type StudentMission = {
    id: number
    student: Student
    mission: ClassMission
    score: number
    isDone: boolean
    isActive: boolean
}

export type StudentMissionCreateDTO = Omit<StudentMission, 'id'>