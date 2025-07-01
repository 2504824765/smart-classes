<script setup lang="tsx">
import { ContentWrap } from '@/components/ContentWrap'
import { useI18n } from '@/hooks/web/useI18n'
import { Table, TableColumn } from '@/components/Table'
import { getTableListApi } from '@/api/table'
import { TableData } from '@/api/table/types'
import { ref, h, reactive } from 'vue'
import { ElTag } from 'element-plus'
import { BaseButton } from '@/components/Button'
import { useRouter } from 'vue-router'
import { createClassMissionApi } from '@/api/mission'
import { ElMessage } from 'element-plus'

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
    field: 'cid',
    label: t('task.cid')
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
    field: 'submit_method',
    label: t('task.submit_method')
  },
  {
    field: 'score',
    label: t('task.score')
  },
  {
    field: 'action',
    label: t('tableDemo.action'),
    slots: {
      default: (data) => {
        return (
          <BaseButton type="primary" onClick={() => actionFn(data)}>
            {t('tableDemo.action')}
          </BaseButton>
        )
      }
    }
  }
]

const loading = ref(true)

const tableDataList = ref<TableData[]>([])

const getTableList = async (params?: Params) => {
  const res = await getTableListApi(
    params || {
      pageIndex: 1,
      pageSize: 10
    }
  )
    .catch(() => {})
    .finally(() => {
      loading.value = false
    })
  if (res) {
    tableDataList.value = res.data.list
  }
}

getTableList()

const actionFn = (data: any) => {
  console.log(data)
}

const createMission = () => {
  router.push('/teacher/CreateMission')
}
// const handleSubmit = async () => {
//   // @ts-ignore
//   await formRef.value?.validate()
//   await createClassMissionApi(formData)
//   ElMessage.success('创建成功')
//   // 你可以在这里加跳转、刷新等逻辑
// }
</script>

<template>
  <el-text class="mx-1" size="large">这是 教师-任务 页面</el-text>
  <ContentWrap :title="t('tableDemo.table')" :message="t('tableDemo.tableDes')">
    <BaseButton type="primary" @click="createMission" style="margin-bottom: 16px">
      创建任务
    </BaseButton>
    <BaseButton type="primary" @click="getTableList" style="margin-bottom: 16px">刷新</BaseButton>
    <Table
      :columns="columns"
      :data="tableDataList"
      :loading="loading"
      :defaultSort="{ prop: 'display_time', order: 'descending' }"
    />
  </ContentWrap>
</template>
