<script setup lang="tsx">
import { ContentWrap } from '@/components/ContentWrap'
import { useI18n } from '@/hooks/web/useI18n'
import { Table, TableColumn } from '@/components/Table'
import { getTableListApi } from '@/api/table'
import { TableData } from '@/api/table/types'
import { ref, h, reactive, computed } from 'vue'
import { ElTag } from 'element-plus'
import { BaseButton } from '@/components/Button'
import { useRouter } from 'vue-router'
import {
  addClassMissionApi,
  getallClassMissionApi,
  updateClassMissionApi,
  deleteClassMissionApi
} from '@/api/classMission/index'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ClassMission,
  ClassMissionCreateDTO,
  ClassMissionUpdateDTO
} from '@/api/classMission/types'
import { getAllClassesApi } from '@/api/classes/index'
import type { Classes } from '@/api/classes/types'

interface Params {
  pageIndex?: number
  pageSize?: number
}

const router = useRouter()
const { t } = useI18n()

//| 编号(id) | 课程编号(cid) | 类型(type)| 说明(description) | 截止时间(deadline，varchar(100)) | 提交方式(submit_method) | 得分(score)
const columns: TableColumn[] = [
  {
    field: 'id',
    label: t('task.id')
  },
  {
    field: 'classes.name',
    label: '课程名称',
    formatter: (row) => row.classes?.name || ''
  },
  {
    field: 'type',
    label: t('task.type')
  },
  {
    field: 'description',
    label: t('task.description'),
    sortable: true
  },
  {
    field: 'deadline',
    label: t('task.deadline')
  },
  {
    field: 'score',
    label: t('task.score')
  },
  {
    field: 'action',
    label: t('tableDemo.action'),
    width: 180,
    slots: {
      default: (data) => (
        <>
          <BaseButton type="primary" size="small" onClick={() => onEdit(data.row)}>
            编辑
          </BaseButton>
          <BaseButton type="danger" size="small" onClick={() => onDelete(data.row)}>
            删除
          </BaseButton>
        </>
      )
    }
  }
]

const loading = ref(false)
const tableDataList = ref<ClassMission[]>([])

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = computed(() => tableDataList.value.length)
const pagedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return tableDataList.value.slice(start, end)
})

const getTableList = async () => {
  loading.value = true
  try {
    const res = await getallClassMissionApi()
    tableDataList.value = res.data || []
  } finally {
    loading.value = false
  }
}

getTableList()

// 弹窗表单相关
const dialogVisible = ref(false)
const isEdit = ref(false)
const formData = reactive<Partial<ClassMissionCreateDTO & { id?: number }>>({
  deadline: ''
})

const allClasses = ref<Classes[]>([])
const classOptions = ref<{ label: string; value: number }[]>([])

const loadClasses = async () => {
  const res = await getAllClassesApi()
  allClasses.value = res.data || []
  classOptions.value = allClasses.value.map((c) => ({ label: c.name, value: c.id }))
}

loadClasses()

const openCreate = () => {
  isEdit.value = false
  Object.assign(formData, { type: '', description: '', deadline: '', score: 0, cid: undefined, id: undefined })
  dialogVisible.value = true
}

const onEdit = (row: ClassMission) => {
  isEdit.value = true
  Object.assign(formData, {
    id: row.id,
    type: row.type,
    description: row.description,
    deadline: typeof row.deadline === 'string' ? row.deadline : '',
    score: row.score,
    cid: (row.classes as any)?.id || (row as any).cid
  })
  dialogVisible.value = true
}

const onDelete = async (row: ClassMission) => {
  await ElMessageBox.confirm(`确定要删除任务「${row.description}」吗？`, '提示', {
    type: 'warning'
  })
  await deleteClassMissionApi(row.id)
  ElMessage.success('删除成功')
  getTableList()
}

const handleSubmit = async () => {
  if (isEdit.value) {
    await updateClassMissionApi(formData as ClassMissionUpdateDTO)
    ElMessage.success('编辑成功')
  } else {
    await addClassMissionApi(formData as ClassMissionCreateDTO)
    ElMessage.success('创建成功')
  }
  dialogVisible.value = false
  getTableList()
}
</script>

<template>
  <ContentWrap>
    <div style="display: flex; align-items: center; gap: 16px; margin-bottom: 16px">
      <span style="font-size: 18px; font-weight: bold">{{ t('teacher.mission') }}</span>
      <!-- <el-button @click="getTableList" style="margin-left: 30px">刷新</el-button> -->
    </div>
    <BaseButton type="primary" @click="openCreate" style="margin-bottom: 16px" round>
      <Icon icon="ep:plus" class="mr-5px" />
      创建任务
    </BaseButton>
    <Table
      :columns="columns"
      :data="pagedData"
      :loading="loading"
      :defaultSort="{ prop: 'id', order: 'descending' }"
      v-model:currentPage="currentPage"
      v-model:pageSize="pageSize"
      :pagination="{ total }"
    />
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑任务' : '创建任务'" width="500px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="课程" prop="cid">
          <el-select v-model="formData.cid" placeholder="请选择课程">
            <el-option
              v-for="item in classOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-input v-model="formData.type" placeholder="请输入类型" />
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model="formData.description" placeholder="请输入说明" />
        </el-form-item>
        <el-form-item label="截止时间">
          <el-date-picker
            v-model="formData.deadline"
            type="datetime"
            placeholder="请选择截止时间"
            format="yyyy-MM-dd HH:mm:ss"
            value-format="yyyy-MM-dd HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="得分">
          <el-input-number v-model="formData.score" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">{{ isEdit ? '保存' : '创建' }}</el-button>
      </template>
    </el-dialog>
  </ContentWrap>
</template>
