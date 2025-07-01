<script setup lang="tsx">
import { ref, reactive, onMounted, unref } from 'vue'
import { ElTree, ElMessage } from 'element-plus'
import { ContentWrap } from '@/components/ContentWrap'
import { Table, TableColumn } from '@/components/Table'
import { useTable } from '@/hooks/web/useTable'
import { getAllDeptApi, getDeptByIdApi, getMembersByDeptIdApi } from '@/api/department/index'

import { Department } from '@/api/department/types'

const deptTree = ref<Department[]>([])
const selectedDepartmentId = ref<number | null>(null)

const treeProps = {
  label: 'name',
  children: 'children'
}

const buildDepartmentTree = (depts: Department[]): Department[] => {
  const map = new Map<number, any>()
  depts.forEach(d => map.set(d.id, { ...d, children: [] }))
  const tree: Department[] = []
  for (const dept of depts) {
    const node = map.get(dept.id)
    if (dept.parentId === 0) {
      tree.push(node)
    } else {
      const parent = map.get(dept.parentId)
      parent?.children.push(node)
    }
  }
  return tree
}

const fetchDepartmentTree = async () => {
  const res = await getAllDeptApi()
  deptTree.value = buildDepartmentTree(res.data)
}

// 成员表格逻辑
const { tableRegister, tableMethods, tableState } = useTable({
  fetchDataApi: async () => {
    if (!selectedDepartmentId.value) return { list: [], total: 0 }
    const res = await getMembersByDepartmentApi(selectedDepartmentId.value)
    return {
      list: res.data,
      total: res.data.length
    }
  }
})

const { dataList, total, loading, currentPage, pageSize } = tableState
const { setProps } = tableMethods

const columns = reactive<TableColumn[]>([
  { field: 'name', label: '姓名' },
  { field: 'username', label: '账号' },
  { field: 'email', label: '邮箱' },
  { field: 'gender', label: '性别' },
  { field: 'gpa', label: 'GPA' }
])

const handleDepartmentClick = (node: Department) => {
  selectedDepartmentId.value = node.id
  tableMethods.refresh()
}

onMounted(fetchDepartmentTree)
</script>

<template>
  <ContentWrap title="组织管理">
    <div style="display: flex; gap: 16px">
      <el-card style="width: 260px; max-height: 600px; overflow: auto">
        <el-tree
          :data="deptTree"
          :props="treeProps"
          highlight-current
          node-key="id"
          @node-click="handleDepartmentClick"
        />
      </el-card>
      <div style="flex: 1">
        <Table
          v-model:pageSize="pageSize"
          v-model:currentPage="currentPage"
          :columns="columns"
          :data="dataList"
          :loading="loading"
          :pagination="{ total }"
          row-key="id"
          @register="tableRegister"
        />
      </div>
    </div>
  </ContentWrap>
</template>
