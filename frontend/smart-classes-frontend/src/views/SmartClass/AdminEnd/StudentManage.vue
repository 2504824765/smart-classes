<script setup lang="tsx">
import { ContentWrap } from '@/components/ContentWrap'
import { Table, TableColumn, TableSlotDefault } from '@/components/Table'
import { BaseButton } from '@/components/Button'
import { useTable } from '@/hooks/web/useTable'
import { ElMessageBox, ElMessage } from 'element-plus'
import { getAllStudentApi, deleteStudentApi } from '@/api/student/index'
import type { Student } from '@/api/student/types' // 或直接定义在本文件中

const { tableRegister, tableMethods, tableState } = useTable({
  fetchDataApi: async () => {
    const res = await getAllStudentApi()
    return {
      list: res.data,
      total: res.data.length
    }
  }
})

const { dataList, total, currentPage, pageSize, loading } = tableState
const { setProps, refresh } = tableMethods

const handleDelete = async (row: Student) => {
  try {
    await ElMessageBox.confirm(`确认删除学生「${row.name}」吗？`, '提示', { type: 'warning' })
    await deleteStudentApi(row.id)
    ElMessage.success('删除成功')
    refresh()
  } catch {
    ElMessage.info('取消删除')
  }
}

setProps({
  columns: [
    { field: 'id', label: '学号', width: 100 },
    { field: 'name', label: '姓名' },
    { field: 'gender', label: '性别', width: 80 },
    { field: 'dept', label: '学院' },
    {
      field: 'gpa',
      label: '绩点',
      width: 80,
      formatter: (_: Recordable, __: TableColumn, value: number) => {
        return value.toFixed(2)
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
                onClick={() => console.log('编辑学生', data.row)}
              >
                编辑
              </BaseButton>
              <BaseButton
                type="danger"
                size="small"
                onClick={() => handleDelete(data.row as Student)}
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
  <ContentWrap title="学生管理">
    <Table
      v-model:currentPage="currentPage"
      v-model:pageSize="pageSize"
      row-key="id"
      :data="dataList"
      :loading="loading"
      :columns="[]"
      :pagination="{ total }"
      @register="tableRegister"
      @refresh="refresh"
    />
  </ContentWrap>
</template>
