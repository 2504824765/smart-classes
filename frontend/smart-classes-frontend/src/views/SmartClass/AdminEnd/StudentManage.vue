<script setup lang="tsx">
import { ContentWrap } from '@/components/ContentWrap'
import { Table, TableColumn, TableSlotDefault } from '@/components/Table'
import { BaseButton } from '@/components/Button'
import { useTable } from '@/hooks/web/useTable'
import { ElMessageBox, ElMessage } from 'element-plus'
import { getAllStudentApi, deleteStudentApi } from '@/api/student/index'
import type { Student } from '@/api/student/types' // 或直接定义在本文件中
import { useRouter } from 'vue-router'

const { push } = useRouter()
const { tableRegister, tableMethods, tableState } = useTable({
  fetchDataApi: async () => {
    const res = await getAllStudentApi()
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
    { field: 'department', label: '学院', formatter: (_: Recordable, __: TableColumn, value: any) => value?.name || value },
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
                onClick={() => push({ path: '/admin/studentManage/form', query: { id: data.row.id } })}
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
    <ElButton
      type="primary"
      @click="push({ path: '/admin/studentManage/form' })"
      style="float: right; margin-bottom: 10px"
      >添加学生</ElButton
    >
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
