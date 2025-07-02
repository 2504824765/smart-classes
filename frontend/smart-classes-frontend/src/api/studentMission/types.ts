export type StudentMission = {
    id: number
    studentId: number
    missionId: number
    score: number
    isDone: boolean
    isActive: boolean
}

export type StudentMissionCreateDTO = Omit<StudentMission, 'id'>