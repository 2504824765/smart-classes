<script setup lang="tsx">
import { Form, FormSchema } from '@/components/Form'
import { ContentWrap } from '@/components/ContentWrap'
import { useForm } from '@/hooks/web/useForm'
import { BaseButton } from '@/components/Button'
import { ElMessage } from 'element-plus'
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { addClassMissionApi } from '@/api/classMission/index'
import { PREFIX } from '@/constants'
import type { ResourceCreateDTO } from '@/api/resource/types'
import { addResourceApi } from '@/api/resource/index'
import { uploadResourcesApi } from '@/api/oss/index'
import type { ClassMissionCreateDTO } from '@/api/classMission/types'
import { addStudentMission } from '@/api/studentMission'
import { getAssociatedByCidApi } from '@/api/studentClasses'
import { StudentMissionCreateDTO } from '@/api/studentMission/types'
import { createClassMissionResource } from '@/api/classMissionResource'

const route = useRoute()
const { push } = useRouter()
const router = useRouter()
const redirectPath = route.query.redirect as string || '/course/list' // fallback 默认返回某页
// 从路由中获取课程 ID
const classId = Number(route.query.classId)
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
      valueFormat: 'YYYY-MM-DD HH:mm:ss',
      // 禁用今天以前的日期时间
      disabledDate: (time: Date) => {
        return time.getTime() < new Date(new Date().setHours(0, 0, 0, 0)).getTime()
      }
    },
    formItemProps: {
      required: true
    }
  },
  {
    field: 'submit_method',
    label: '提交说明（可选）',
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
      limit: 5,
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
  console.log('classId',classId)
  if(!classId) {
    ElMessage.warning('课程ID不存在')
    return
  }

  await elForm.validate(async (valid) => {
    if (!valid) {
      ElMessage.warning('请检查表单填写是否完整')
      return
    }

    try {
      const formData = await getFormData<ClassMissionCreateDTO>()

      // 提交任务数据，合并 resourceId
      const missionToSubmit = {
        ...formData,
        cid: classId,
      }
      console.log('missionToSubmit',missionToSubmit)

      const classMissionRes = await addClassMissionApi(missionToSubmit)
      ElMessage.success('任务创建成功')

      if (pendingResources.value.length > 0) {
        for (const resFile of pendingResources.value) {
          const uploadRes = await uploadResourcesApi(resFile.file, '任务资源')
          console.log('uploadRes', uploadRes)

          const filePath = uploadRes.data
          const newRes = {
            path: filePath.replace(PREFIX, ''),
            classMissionId: classMissionRes.data.id
          }

          const savedRes = await createClassMissionResource(newRes)
          console.log('savedRes', savedRes)
        }

        ElMessage.success('所有资源上传成功')
      }

      console.log('classMissionRes',classMissionRes)
      const studentClassRes = await getAssociatedByCidApi(classId)
      console.log('studentClassRes',studentClassRes)
      if (!studentClassRes.data || studentClassRes.data.length === 0) {
        ElMessage.warning('当前课程没有学生选课记录，学生任务无法创建')
        return
      }
      try {
        // 2. 遍历学生，构造每个 StudentMissionCreateDTO 并创建
        const createPromises = studentClassRes.data.map(async (record: any) => {
          const dto: StudentMissionCreateDTO = {
            studentId: record.student.id,
            classMissionId: classMissionRes.data.id, 
            score: 0,
            isDone: false,
            isActive: true,
            reportUrl: '',
            aiCommentUrl: '',
          }
          console.log('dto',dto)
          const res = await addStudentMission(dto)
          console.log('res',res)
          return res
        })

        await Promise.all(createPromises)
        ElMessage.success('学生任务创建成功')
        router.push(redirectPath)
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

const handleBack = () => {
  const redirect = router.currentRoute.value.query.redirect as string
  if (redirect) {
    router.push(redirect)
  } else {
    router.back()
  }
}
</script>

<template>
  <el-page-header content="新增任务" @back="handleBack" />
  <ContentWrap>
    <Form :schema="missionFormSchema" @register="formRegister" />

    <div style="margin-top: 16px; display: flex; gap: 12px">
      <BaseButton type="primary" @click="handleSubmit">提交</BaseButton>
    </div>
  </ContentWrap>
</template>