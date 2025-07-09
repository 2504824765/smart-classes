import request from '@/axios'
/**
 * 上传资源文件（带文件夹名 message）
 * @param file 上传的资源文件
 * @param message 资源所属的文件夹名
 * @returns 上传后资源的访问 URL
 */
export const uploadResourcesApi = (file: File, message: string): Promise<IResponse<string>> => {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('message', message)
  return request.post({
    url: '/api/oss/uploadResource',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 上传知识图谱文件
 * @param file 上传的图谱文件
 * @param id 课程 ID
 * @returns 图谱文件的访问 URL
 */
export const uploadGraphApi = (file: File, id: number): Promise<IResponse<{ url: string }>> => {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('id', id.toString())
  return request.post({ url: '/api/oss/uploadGraph', data: formData })
}

/**
 * 上传课程封面图
 * @param file 上传的图片文件
 * @param id 课程 ID
 * @returns 图片 URL
 */
export const uploadImageApi = (file: File, id: number): Promise<IResponse<{ url: string }>> => {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('id', id.toString())
  return request.post({ url: '/api/oss/uploadImage', data: formData })
}

/**
 * 获取课程的全部资源文件 URL 列表
 * @param cid 课程 ID
 * @returns 文件 URL 列表
 */
export const getAllResourcesByClassIdApi = (
  cid: number
): Promise<IResponse<{ urls: string[] }>> => {
  return request.get({ url: '/api/oss/getAllByClassId', params: { cid } })
}
