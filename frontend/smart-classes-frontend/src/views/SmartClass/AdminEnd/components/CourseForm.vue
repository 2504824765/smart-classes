<script setup lang="tsx">
import { Form, FormSchema } from '@/components/Form'
import { ContentWrap } from '@/components/ContentWrap'
import { useForm } from '@/hooks/web/useForm'
import { BaseButton } from '@/components/Button'
import { ElMessage } from 'element-plus'
import { ref, reactive, onMounted, computed } from 'vue'
import { Classes, ClassesCreateDTO } from '@/api/classes/types'
import { addClassApi, updateClassApi, getClassesByIdApi } from '@/api/classes'
import { PendingUploadResource, ResourceCreateDTO, Resource } from '@/api/resource/types'
import { addResourceApi, getResourceByClassIdApi, getResourceByIdApi, deleteResourceApi } from '@/api/resource'
import { uploadResourcesApi } from '@/api/oss'
import { Teacher } from '@/api/teacher/types'
import { getAllTeacherApi } from '@/api/teacher'
import { useRoute, useRouter } from 'vue-router'
import { ElUpload } from 'element-plus'
import { getClassMissionByCidApi } from '@/api/classMission'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const isEdit = ref(false)
const courseId = ref<number | null>(null)

//待上传的资源
const pendingResources = ref<PendingUploadResource[]>([])
// 绑定上传的资源（uploadedResources 是前面上传时填充的）
const uploadedResources = ref<ResourceCreateDTO[]>([])
const allTeachers = ref<Teacher[]>([]) // 所有教师原始数据
const teacherOptions = ref<{ label: string; value: number }[]>([]) // 绑定 Select
const teacherLoading = ref(false)
const missionResources = ref<Resource[]>([])

const isAddMode = computed(() => !isEdit.value)
const isEditMode = computed(() => isEdit.value)

const isFile = (f: any): f is File => typeof File !== 'undefined' && f instanceof File;

const courseFormSchema = reactive<FormSchema[]>([
  {
    field: 'name',
    label: '课程名称',
    component: 'Input',
    formItemProps: {
      required: true
    }
  },
  {
    field: 'teacherId',
    label: '授课教师',
    component: 'Select',
    componentProps: {
      filterable: true,
      clearable: true,
      loading: teacherLoading,
      options: teacherOptions,
      filterMethod: (query: string) => {
        if (!query) {
          teacherOptions.value = allTeachers.value.map((t) => ({
            label: t.name,
            value: t.id
          }))
          return
        }
        teacherOptions.value = allTeachers.value
          .filter(
            (t) =>
              t.name.includes(query) ||
              t.department?.name?.includes(query) ||
              String(t.id).includes(query)
          )
          .map((t) => ({
            label: t.name,
            value: t.id
          }))
      }
    },
    formItemProps: {
      required: true
    }
  },
  {
    field: 'credit',
    label: '学分',
    component: 'InputNumber',
    componentProps: {
      min: 0,
      max: 10
    }
  },
  {
    field: 'classHours',
    label: '课时',
    component: 'InputNumber',
    componentProps: {
      min: 0
    }
  },
  {
    field: 'description',
    label: '课程简介',
    component: 'Input',
    componentProps: {
      type: 'textarea',
      rows: 4
    }
  },
  {
    field: 'active',
    label: '是否开放',
    component: 'Switch',
    value: true
  },
  {
    field: 'files',
    label: '上传课程资料',
    component: 'Upload',
    componentProps: {
      multiple: true,
      limit: 3,
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
        default: () => <BaseButton type="primary">点击上传</BaseButton>
      }
    },
    remove: !isEditMode.value
  }
])

const { formRegister, formMethods } = useForm()
const { getFormData, getElFormExpose } = formMethods

onMounted(async () => {
  teacherLoading.value = true
  try {
    const res = await getAllTeacherApi()
    allTeachers.value = res.data
    teacherOptions.value = res.data.map((t) => ({
      label: t.name,
      value: t.id
    }))

    // 编辑模式：拉取课程详情
    const id = route.query.id
    if (id) {
      isEdit.value = true
      courseId.value = Number(id)
      try {
        const courseRes = await getClassesByIdApi(courseId.value)
        const course = courseRes.data
        formMethods.setValues({
          name: course.name,
          teacherId: course.teacher?.id,
          credit: course.credit,
          classHours: course.classHours,
          description: course.description,
          active: course.active,
        })
        // 直接拉取课程所有资源
        const resourceRes = await getResourceByClassIdApi(courseId.value)
        missionResources.value = resourceRes.data
      } catch (error) {
        ElMessage.error('获取课程信息失败')
        console.error('获取课程信息错误：', error)
      }
    } else {
      isEdit.value = false
      courseId.value = null
    }
  } finally {
    teacherLoading.value = false
  }
})

// 提交表单
const handleSubmit = async () => {
  const elForm = await getElFormExpose()
  if (!elForm) return

  await elForm.validate(async (valid) => {
    if (!valid) {
      ElMessage.warning('请检查表单填写是否完整')
      return
    }

    try {
      const formData = await getFormData<ClassesCreateDTO>()
      // 强制teacherId为number类型，防止为字符串
      if (formData.teacherId) {
        formData.teacherId = Number(formData.teacherId)
      }
      if (isEdit.value) {
        if (!courseId.value) {
          ElMessage.error('课程ID缺失，无法编辑')
          return
        }
        await updateClassApi({ ...formData, id: courseId.value })
        ElMessage.success('课程修改成功')
      } else {
        await addClassApi(formData)
        ElMessage.success('课程创建成功')
      }

      // 处理资源上传（仅在新增模式下）
      if (!isEdit.value && pendingResources.value.length > 0) {
        // 获取新创建的课程ID
        const newCourseId = courseId.value || 0

        for (const resource of pendingResources.value) {
          const uploadRes = await uploadResourcesApi(resource.file, '课程资料')
          const filePath = uploadRes.data

          uploadedResources.value.push({
            name: resource.name,
            path: filePath,
            type: resource.type,
            description: resource.description,
            classId: newCourseId
          })
        }

        // 插入数据库资源记录
        for (const resource of uploadedResources.value) {
          await addResourceApi(resource)
        }
      }

      router.push({ path: '/admin/courseManage' })
    } catch (err) {
      ElMessage.error('提交失败，请重试')
      console.error('提交错误：', err)
    }
  })
}

// 新增上传文件相关逻辑
const handleFileUpload = async (file: File) => {
  const fileName = file.name
  const fileType = fileName.split('.').pop() || ''
  // 1. 上传文件到后端，获取url
  const uploadRes = await uploadResourcesApi(file, '课程资料')
  const filePath = uploadRes.data
  // 2. 保存资源信息到数据库
  const resource = await addResourceApi({
    name: fileName,
    path: filePath,
    type: fileType,
    description: fileName,
    classId: courseId.value || 0
  })
  // 3. 显示在底部文件列表
  uploadedResources.value.push(resource.data)
  ElMessage.success(`已上传：${fileName}`)
}

// 课程资源上传（编辑模式下）
const handleResourceUpload = async (file: File) => {
  if (!courseId.value) {
    ElMessage.error('课程ID缺失，无法上传资源')
    return
  }
  try {
    const fileName = file.name
    const fileType = fileName.split('.').pop() || ''
    // 1. 上传文件到后端，获取url
    const uploadRes = await uploadResourcesApi(file, '课程资料')
    const filePath = uploadRes.data
    console.log(uploadRes)
    // 2. 保存资源信息到数据库
    await addResourceApi({
      name: fileName,
      path: filePath,
      type: fileType,
      description: fileName,
      classId: courseId.value
    })
    ElMessage.success(`已上传：${fileName}`)
    await fetchResourceList()
  } catch (err) {
    ElMessage.error('上传失败')
    console.error(err)
  }
}

const fetchResourceList = async () => {
  if (!courseId.value) return
  const resourceRes = await getResourceByClassIdApi(courseId.value)
  missionResources.value = resourceRes.data
}

const handleDeleteResource = async (id: number) => {
  ElMessageBox.confirm('确定要删除该资源吗？', '提示', { type: 'warning' })
    .then(async () => {
      await deleteResourceApi(id)
      ElMessage.success('删除成功')
      await fetchResourceList()
    })
    .catch(() => {})
}
</script>

<template>
  <ContentWrap :title="isEdit ? '编辑课程' : '新增课程'">
    <Form :schema="courseFormSchema" @register="formRegister" />
    <div v-if="isEdit">
      <div style="margin: 24px 0 0 0;">
        <div style="font-weight: bold; margin-bottom: 8px; display: flex; align-items: center; gap: 16px;">
          <span>课程所有资源：</span>
          <el-upload
            :show-file-list="false"
            :before-upload="() => false"
            :on-change="(file) => { if (isFile(file.raw)) handleResourceUpload(file.raw) }"
            accept="*"
          >
            <BaseButton type="primary" size="small">上传资源</BaseButton>
          </el-upload>
        </div>
        <div v-if="missionResources.length" style="display: flex; flex-wrap: wrap; gap: 16px;">
          <div v-for="file in missionResources" :key="file.id" style="border: 1px solid #eee; border-radius: 6px; padding: 12px 18px; min-width: 180px; background: #fafbfc; position: relative;">
            <div><b>文件名：</b>{{ file.name }}</div>
            <div><b>类型：</b>{{ file.type }}</div>
            <div v-if="file.path"><a :href="file.path" target="_blank" style="color: #409EFF;">下载/查看</a></div>
            <el-button type="danger" size="small" style="position: absolute; top: 8px; right: 8px;" @click="handleDeleteResource(file.id)">删除</el-button>
          </div>
        </div>
      </div>
    </div>
    <BaseButton type="primary" style="margin-top: 16px" @click="handleSubmit">
      {{ isEdit ? '更新' : '提交' }}
    </BaseButton>
  </ContentWrap>
</template>
