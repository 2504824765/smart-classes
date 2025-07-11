<script setup lang="tsx">
import { ContentWrap } from '@/components/ContentWrap'
import { Table, TableColumn, TableSlotDefault } from '@/components/Table'
import { BaseButton } from '@/components/Button'
import { useTable } from '@/hooks/web/useTable'
import { ElMessageBox, ElMessage, ElTag } from 'element-plus'
import { getAllClassesApi, deleteClassApi } from '@/api/classes/index'
import type { Classes } from '@/api/classes/types'
import { useRouter } from 'vue-router'

const { push } = useRouter()

const { tableRegister, tableMethods, tableState } = useTable({
  fetchDataApi: async () => {
    const res = await getAllClassesApi()
    const allData = res.data
    const start = (tableState.currentPage.value - 1) * tableState.pageSize.value
    const end = start + tableState.pageSize.value
    return {
      list: allData.slice(start, end),
      total: allData.length
    }
  }
})

const { dataList, total, currentPage, pageSize, loading } = tableState
const { setProps, refresh } = tableMethods

const handleDelete = async (row: Classes) => {
  try {
    await ElMessageBox.confirm(`确认删除课程「${row.name}」吗？`, '提示', { type: 'warning' })
    await deleteClassApi(row.id)
    ElMessage.success('删除成功')
    refresh()
  } catch {
    ElMessage.info('取消删除')
  }
}

const handleEdit = (row: Classes) => {
  push({ path: '/admin/courseManage/form', query: { id: row.id } })
}

setProps({
  columns: [
    { field: 'id', label: '课程ID', width: 80 },
    { field: 'name', label: '课程名称' },
    { field: 'credit', label: '学分', width: 80 },
    { field: 'classHours', label: '课时', width: 80 },
    {
      field: 'teacher',
      label: '授课教师',
      width: 120,
      formatter: (_: Recordable, __: TableColumn, cellValue: any) => {
        return cellValue?.name || '未分配'
      }
    },
    {
      field: 'isActive',
      label: '是否开课',
      width: 100,
      formatter: (_: Recordable, __: TableColumn, cellValue: boolean) => {
        return (
          <ElTag type={cellValue ? 'success' : 'danger'}>{cellValue ? '已开课' : '未开课'}</ElTag>
        )
      }
    },
    {
      field: 'description',
      label: '课程简介',
      formatter: (_: Recordable, __: TableColumn, cellValue: string) => {
        return cellValue?.length > 30 ? cellValue.slice(0, 30) + '...' : cellValue
      }
    },
    {
      field: 'action',
      label: '操作',
      width: 180,
      slots: {
        default: (data: TableSlotDefault) => {
          return (
            <>
              <BaseButton
                type="primary"
                size="small"
                onClick={() => handleEdit(data.row as Classes)}
              >
                编辑
              </BaseButton>
              <BaseButton
                type="danger"
                size="small"
                onClick={() => handleDelete(data.row as Classes)}
              >
                删除
              </BaseButton>
            </>
          )
        }
      }
    }
  ]
})
</script>

<template>
  <ContentWrap title="课程管理">
    <ElButton
      type="primary"
      @click="push({ path: '/admin/courseManage/form' })"
      style="float: right; margin-bottom: 10px"
      >添加课程</ElButton
    >
    <Table
      v-model:currentPage="currentPage"
      v-model:pageSize="pageSize"
      row-key="id"
      :data="dataList"
      :loading="loading"
      :pagination="{ total }"
      @register="tableRegister"
      @refresh="refresh"
    />
  </ContentWrap>
</template>
