<template>
  <ContentWrap title="组织管理">
    <div class="org-layout">
      <!-- 部门树 -->
      <el-card class="org-tree">
        <el-tree
          :data="deptTree"
          :props="treeProps"
          node-key="id"
          highlight-current
          @node-click="handleDeptClick"
        />
      </el-card>

      <!-- 成员表格 -->
      <div class="org-table">
        <Table @register="registerTable" />
      </div>
    </div>
  </ContentWrap>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElTree } from 'element-plus'
import { ContentWrap } from '@/components/ContentWrap'
import { Table, useTable } from '@/components/Table'
import { getAllDeptApi } from '@/api/department/index'
import { getMembersByDeptApi } from '@/api/member' // 你需要提供这个接口
import type { Dept } from '@/api/dept/types'

const deptTree = ref<Dept[]>([])
const selectedDeptId = ref<number | null>(null)

const treeProps = {
  label: 'name',
  children: 'children'
}

// 将 flat list 转为 tree 结构
function buildDeptTree(depts: Dept[]): Dept[] {
  const map = new Map<number, any>()
  depts.forEach(d => map.set(d.id, { ...d, children: [] }))
  const tree: Dept[] = []

  for (const dept of depts) {
    if (dept.parentId === 0) {
      tree.push(map.get(dept.id))
    } else {
      const parent = map.get(dept.parentId)
      parent?.children.push(map.get(dept.id))
    }
  }

  return tree
}

const fetchDeptTree = async () => {
  const res = await getAllDeptApi()
  deptTree.value = buildDeptTree(res.data)
}

// 表格初始化
const [registerTable, { reload }] = useTable({
  title: '成员列表',
  api: async () => {
    if (!selectedDeptId.value) return []
    const res = await getMembersByDeptApi(selectedDeptId.value)
    return res.data
  },
  columns: [
    { field: 'name', label: '姓名' },
    { field: 'email', label: '邮箱' },
    { field: 'phone', label: '电话' },
    { field: 'title', label: '职称' }
  ],
  actionColumn: {
    width: 150,
    actions: [
      { label: '编辑', type: 'primary', onClick: ({ row }) => handleEdit(row) },
      { label: '删除', type: 'danger', onClick: ({ row }) => handleDelete(row.id) }
    ]
  }
})

const handleDeptClick = (node: Dept) => {
  selectedDeptId.value = node.id
  reload()
}

const handleEdit = (row: any) => {
  // 打开编辑表单
  console.log('编辑成员', row)
}

const handleDelete = async (id: number) => {
  // 调用删除接口
  console.log('删除成员 ID', id)
  reload()
}

onMounted(fetchDeptTree)
</script>

<style scoped>
.org-layout {
  display: flex;
  gap: 16px;
}
.org-tree {
  width: 260px;
  max-height: 600px;
  overflow-y: auto;
}
.org-table {
  flex: 1;
}
</style>
