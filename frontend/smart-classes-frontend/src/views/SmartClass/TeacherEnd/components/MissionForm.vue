<script setup lang="tsx">
import { Form, FormSchema } from '@/components/Form'
import { ContentWrap } from '@/components/ContentWrap'
import { useForm } from '@/hooks/web/useForm'
import { BaseButton } from '@/components/Button'
import { ElMessage } from 'element-plus'
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { addClassMissionApi } from '@/api/classMission/index'
import type { ResourceCreateDTO } from '@/api/resource/types'
import { addResourceApi } from '@/api/resource/index'
import { uploadResourcesApi } from '@/api/oss/index'
import type { ClassMissionCreateDTO } from '@/api/classMission/types'
import { addStudentMission } from '@/api/studentMission'
import { getAssociatedByCidApi } from '@/api/studentClasses'
import { StudentMissionCreateDTO } from '@/api/studentMission/types'

const route = useRoute()

// 从路由中获取课程 ID
const classId = Number(route.query.cid)
// 定义上传等待列表
const pendingResources = ref<PendingUploadResource[]>([])
interface PendingUploadResource {
  name: string
  type: string
  description: string
  file: File
}

const missionFormSchema = reactive<FormSchema[]>([
  {
    field: 'type',
    label: '任务类型',
    component: 'Select',
    componentProps: {
      placeholder: '请选择任务类型',
      options: [
        { label: 'PPT浏览', value: 'PPT浏览' },
        { label: '章节资料阅读', value: '章节资料阅读' },
        { label: '视频观看', value: '视频观看' },
        { label: '测试答题', value: '测试答题' },
        { label: '实践项目', value: '实践项目' }
      ]
    },
    formItemProps: {
      required: true
    }
  },
  {
    field: 'description',
    label: '任务说明',
    component: 'Input',
    componentProps: {
      type: 'textarea',
      rows: 3
    },
    formItemProps: {
      required: true
    }
  },
  {
    field: 'deadline',
    label: '截止时间',
    component: 'DatePicker',
    componentProps: {
      type: 'datetime',
      format: 'YYYY-MM-DD HH:mm:ss',
      valueFormat: 'YYYY-MM-DD HH:mm:ss'
    },
    formItemProps: {
      required: true
    }
  },
  {
    field: 'submit_method',
    label: '提交方式',
    component: 'Input'
  },
  {
    field: 'score',
    label: '总分',
    component: 'InputNumber',
    componentProps: {
      min: 0,
      max: 100
    }
  },
  {
    field: 'files',
    label: '任务资源',
    component: 'Upload',
    componentProps: {
      multiple: false,
      limit: 1,
      httpRequest: async (options) => {
        const rawFile = options.file as File
        const fileName = rawFile.name
        const fileType = fileName.split('.').pop() || ''

        pendingResources.value.push({
          name: fileName,
          type: fileType,
          description: fileName,
          file: rawFile
        })

        options.onSuccess?.({}, rawFile)
        ElMessage.success(`已添加到待上传列表：${fileName}`)
      },
      slots: {
        default: () => <BaseButton type="primary">点击上传任务文件</BaseButton>
      }
    }
  }
])

const { formRegister, formMethods } = useForm()
const { getFormData, getElFormExpose } = formMethods

const handleSubmit = async () => {
  const elForm = await getElFormExpose()
  if (!elForm) return

  await elForm.validate(async (valid) => {
    if (!valid) {
      ElMessage.warning('请检查表单填写是否完整')
      return
    }

    try {
      const formData = await getFormData<ClassMissionCreateDTO>()
      let resourceId: number | null = null

      // 上传文件（仅一个）
      if (pendingResources.value.length > 0) {
        const resFile = pendingResources.value[0]
        const uploadRes = await uploadResourcesApi(resFile.file, '任务资源')
        const filePath = uploadRes.data

        const newRes = {
          name: resFile.name,
          path: filePath,
          type: resFile.type,
          description: resFile.description,
          classId: formData.classes.id
        }

        const savedRes = await addResourceApi(newRes)
        resourceId = savedRes.data.id
      }

      // 提交任务数据，合并 resourceId
      const missionToSubmit = {
        ...formData,
        resource: resourceId ?? 0
      }

      const classMissionRes = await addClassMissionApi(missionToSubmit)
      ElMessage.success('任务创建成功')
      const studentClassRes = await getAssociatedByCidApi(classId)
      if (!studentClassRes.data || studentClassRes.data.length === 0) {
        ElMessage.warning('当前课程没有学生选课记录，学生任务无法创建')
        return
      }
      try {
        // 2. 遍历学生，构造每个 StudentMissionCreateDTO 并创建
        const createPromises = studentClassRes.data.map((record: any) => {
          const dto: StudentMissionCreateDTO = {
            student: record.student.id, // 👈 只传 student id
            classMission: classMissionRes.data.id, // 👈 只传 classMission id
            score: 0,
            done: false,
            active: true,
            reportUrl: ''
          }
          return addStudentMission(dto)
        })

        await Promise.all(createPromises)
        ElMessage.success('学生任务创建成功')
        // 可选重置
        elForm.resetFields()
        pendingResources.value = []
      } catch (e) {
        console.error('批量创建失败:', e)
        ElMessage.error('学生任务创建失败，请重试')
      }
    } catch (err) {
      ElMessage.error('提交失败，请重试')
      console.error('任务提交错误：', err)
    }
  })
}
</script>

<template>
  <ContentWrap title="新增任务">
    <Form :schema="missionFormSchema" @register="formRegister" />
    <BaseButton type="primary" style="margin-top: 16px" @click="handleSubmit">提交</BaseButton>
  </ContentWrap>
</template>
