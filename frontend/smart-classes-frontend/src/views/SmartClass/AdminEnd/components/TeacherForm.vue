<script setup lang="ts">
import { Form, FormSchema } from '@/components/Form'
import { ContentWrap } from '@/components/ContentWrap'
import { BaseButton } from '@/components/Button'
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useForm } from '@/hooks/web/useForm'
import { createTeacherApi, updateTeacherApi, getTeacherByIdApi } from '@/api/teacher/index'
import { getAllDeptApi } from '@/api/department/index'
import type { TeacherCreateDTO, TeacherUpdateDTO } from '@/api/teacher/types'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const isEdit = ref(false)
const teacherId = ref<number | null>(null)
const deptTreeOptions = ref<any[]>([])

// 平铺转树
function listToTree(list: any[], parentId = 0) {
  return list
    .filter(item => item.parentId === parentId)
    .map(item => ({
      label: item.name,
      value: item.id,
      children: listToTree(list, item.id)
    }))
}

const teacherFormSchema = reactive<FormSchema[]>([
  {
    field: 'username',
    label: '用户名',
    component: 'Input',
    formItemProps: {
      required: true
    },
    componentProps: {
      disabled: isEdit
    }
  },
  {
    field: 'name',
    label: '教师姓名',
    component: 'Input',
    formItemProps: {
      required: true
    }
  },
  {
    field: 'gender',
    label: '性别',
    component: 'Select',
    componentProps: {
      options: [
        { label: '男', value: '男' },
        { label: '女', value: '女' }
      ]
    },
    formItemProps: {
      required: true
    }
  },
  {
    field: 'departmentId',
    label: '所属院系',
    component: 'Cascader',
    componentProps: {
      options: deptTreeOptions,
      props: { checkStrictly: true, emitPath: false },
      clearable: true,
      placeholder: '请选择院系'
    },
    formItemProps: {
      required: true
    }
  }
])

const { formRegister, formMethods } = useForm()
const { getElFormExpose, getFormData, setValues } = formMethods

const loadDepartments = async () => {
  const res = await getAllDeptApi()
  deptTreeOptions.value = listToTree(res.data)
}

const loadTeacher = async (id: number) => {
  const res = await getTeacherByIdApi(id)
  const teacher = res.data
  setValues({
    username: teacher.username,
    name: teacher.name,
    gender: teacher.gender,
    departmentId: (teacher as any).department?.id
  })
}

onMounted(async () => {
  await loadDepartments()
  const id = route.query.id
  if (id) {
    isEdit.value = true
    teacherId.value = Number(id)
    await loadTeacher(teacherId.value)
  } else {
    isEdit.value = false
    teacherId.value = null
  }
})

const handleSubmit = async () => {
  const elForm = await getElFormExpose()
  if (!elForm) return

  await elForm.validate(async (valid) => {
    if (!valid) {
      ElMessage.warning('请完整填写教师信息')
      return
    }
    let formData = await getFormData<TeacherCreateDTO & { departmentId: number | number[] }>()
    // 取多级选择的最后一级id
    if (Array.isArray(formData.departmentId)) {
      formData.departmentId = formData.departmentId[formData.departmentId.length - 1]
    }
    try {
      if (isEdit.value) {
        if (!teacherId.value) {
          ElMessage.error('教师ID缺失，无法编辑')
          return
        }
        await updateTeacherApi({ ...formData, id: teacherId.value } as TeacherUpdateDTO)
        ElMessage.success('教师信息更新成功')
      } else {
        await createTeacherApi(formData)
        ElMessage.success('教师添加成功')
        elForm.resetFields()
      }
      router.push({ path: '/admin/teacherManage' })
    } catch (err) {
      ElMessage.error('操作失败，请稍后重试')
    }
  })
}
</script>

<template>
  <ContentWrap :title="isEdit ? '编辑教师' : '新增教师'">
    <Form :schema="teacherFormSchema" @register="formRegister" />
    <BaseButton type="primary" style="margin-top: 16px" @click="handleSubmit">{{ isEdit ? '保存' : '提交' }}</BaseButton>
    <BaseButton style="margin-top: 16px; margin-left: 8px" @click="() => router.push({ path: '/admin/teacherManage' })">返回</BaseButton>
  </ContentWrap>
</template>
