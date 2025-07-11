import { ClassMissionResource, ClassMissionResourceCreateDTO } from "./types";
import request from "@/axios"

export const getAllClassMissionResourcesByClassMissionId = (classMissionId: number): Promise<IResponse<ClassMissionResource>> => {
  return request.get({ url: `/api/classMissionResource/getAllClassMissionResourceByClassMissionId/${classMissionId}` })
}

export const createClassMissionResource = (classMissionResourceCreateDTO: ClassMissionResourceCreateDTO): Promise<IResponse<ClassMissionResource>> => {
  return request.post({ url: '/api/classMissionResource/create', data: classMissionResourceCreateDTO })
}

export const deleteClassMissionResource = (classMissionResourceId: number) => {
  return request.delete({ url: `/api/classMissionResource/deleteById/${classMissionResourceId}` })
}