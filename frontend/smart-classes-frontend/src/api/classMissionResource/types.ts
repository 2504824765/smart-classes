import { ClassMission } from "../classMission/types"

export type ClassMissionResource = {
    id: number
    classMission: ClassMission
    path: string
    // TODO: Add more fields
}

export type ClassMissionResourceCreateDTO = Omit<ClassMissionResource, "id" | 'classMission'> & {
    classMissionId: number
}