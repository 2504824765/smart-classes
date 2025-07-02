<script setup lang="tsx">
import { ContentWrap } from '@/components/ContentWrap'
import { Table, TableSlotDefault } from '@/components/Table'
import { BaseButton } from '@/components/Button'
import { useTable } from '@/hooks/web/useTable'
import { getAllTeacherApi, deleteTeacherApi } from '@/api/teacher/index'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const { push } = useRouter()

const { tableRegister, tableMethods, tableState } = useTable({
  fetchDataApi: async () => {
    const res = await getAllTeacherApi()
    return {
      list: res.data,
      total: res.data.length
    }
  }
})

const { dataList, total, currentPage, pageSize, loading } = tableState
const { setProps, refresh } = tableMethods

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(`确认删除教师 ${row.name} 吗？`, '提示', { type: 'warning' })
    await deleteTeacherApi(row.id)
    ElMessage.success('删除成功')
    refresh()
  } catch (err) {
    ElMessage.info('取消删除')
  }
}

setProps({
  columns: [
    { field: 'id', label: 'ID', width: 80 },
    { field: 'username', label: '用户名' },
    { field: 'name', label: '姓名' },
    { field: 'gender', label: '性别' },
    { field: 'department', label: '所属院系', formatter: (_: any, __: any, value: any) => value?.name || value },
    {
      field: 'action',
      label: '操作',
      width: 180,
      slots: {
        default: (data: TableSlotDefault) => {
          return (
            <>
              <BaseButton type="primary" size="small" onClick={() => push({ path: '/admin/teacherManage/form', query: { id: data.row.id } })}>
                编辑
              </BaseButton>
              <BaseButton type="danger" size="small" onClick={() => handleDelete(data.row)}>
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
  <ContentWrap title="教师管理">
    <ElButton
      type="primary"
      @click="push({ path: '/admin/teacherManage/form' })"
      style="float: right; margin-bottom: 10px"
      >添加教师</ElButton
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
