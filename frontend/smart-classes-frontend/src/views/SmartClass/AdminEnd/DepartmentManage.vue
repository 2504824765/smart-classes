<script setup lang="tsx">
import { ref, reactive, onMounted, unref } from 'vue'
import { ElTree, ElMessage, ElMessageBox } from 'element-plus'
import { ContentWrap } from '@/components/ContentWrap'
import { Table, TableColumn } from '@/components/Table'
import { useTable } from '@/hooks/web/useTable'
import { getAllDeptApi, getDeptByIdApi, getMembersByDeptIdApi, addDepartmentApi, updateDepartmentApi, deleteDepartmentApi } from '@/api/department/index'

import { Department } from '@/api/department/types'

const deptTree = ref<Department[]>([])
const selectedDepartmentId = ref<number | null>(null)
const allDeptList = ref<Department[]>([])

const treeProps = {
  label: 'name',
  children: 'children'
}

const buildDepartmentTree = (depts: Department[]): Department[] => {
  const map = new Map<number, any>()
  depts.forEach((d) => map.set(d.id, { ...d, children: [] }))
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
  allDeptList.value = res.data // 保存平铺数组
  deptTree.value = buildDepartmentTree(res.data)
}

// 成员表格逻辑
const { tableRegister, tableMethods, tableState } = useTable({
  fetchDataApi: async () => {
    if (!selectedDepartmentId.value) return { list: [], total: 0 }
    const res = await getMembersByDeptIdApi(selectedDepartmentId.value)
    return {
      list: res.data,
      total: res.data.length
    }
  }
})

const columns = reactive<TableColumn[]>([
  { field: 'name', label: '姓名' },
  { field: 'username', label: '账号' },
  { field: 'email', label: '邮箱' },
  { field: 'gender', label: '性别' },
  { field: 'gpa', label: 'GPA' }
])

const detailDialogVisible = ref(false)
const detailData = ref<any>(null)

const handleDepartmentClick = async (node: Department) => {
  selectedDepartmentId.value = node.id
  // 获取部门详情
  const res = await getDeptByIdApi(node.id)
  detailData.value = res.data
  detailDialogVisible.value = true
}

const dialogVisible = ref(false)
const isEdit = ref(false)
const formData = reactive({
  id: null as number | null,
  name: '',
  parentId: 0
})

const parentTreeOptions = ref<any[]>([])
function listToTree(list: any[], parentId = 0) {
  return list
    .filter(item => item.parentId === parentId)
    .map(item => ({
      label: item.name,
      value: item.id,
      children: listToTree(list, item.id)
    }))
}

function findPathToId(tree, targetId, path = []) {
  for (const node of tree) {
    const newPath = [...path, node.id]
    if (node.id === targetId) return newPath
    if (node.children) {
      const res = findPathToId(node.children, targetId, newPath)
      if (res) return res
    }
  }
  return null
}

const openDialog = (type: 'add' | 'edit', node?: Department) => {
  isEdit.value = type === 'edit'
  parentTreeOptions.value = listToTree(allDeptList.value)
  if (isEdit.value && node) {
    Object.assign(formData, node)
    formData.parentId = node.parentId
  } else {
    Object.assign(formData, { id: null, name: '', parentId: 0 })
  }
  dialogVisible.value = true
}

const handleDelete = async (node: Department) => {
  await ElMessageBox.confirm(`确定要删除部门「${node.name}」吗？`, '提示', { type: 'warning' })
  await deleteDepartmentApi(node.id)
  ElMessage.success('删除成功')
  await fetchDepartmentTree()
  tableMethods.refresh()
}

const handleSubmit = async () => {
  if (isEdit.value) {
    await updateDepartmentApi({
      id: formData.id as number,
      name: formData.name,
      parentId: formData.parentId
    })
    ElMessage.success('编辑成功')
  } else {
    const { id, name, parentId } = formData
    await addDepartmentApi({ name, parentId })
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  await fetchDepartmentTree()
  tableMethods.refresh()
}

function getDeptNameById(id) {
  // 递归查找树结构
  const find = (list) => {
    for (const item of list) {
      if (item.value === id) return item.label
      if (item.children) {
        const res = find(item.children)
        if (res) return res
      }
    }
    return ''
  }
  return find(parentTreeOptions.value)
}

onMounted(fetchDepartmentTree)
</script>

<template>
  <ContentWrap title="组织管理">
    <el-card
      style="
        width: 100%;
        min-height: 600px;
        background: #f8fafc;
        box-shadow: 0 4px 24px 0 rgba(0,0,0,0.08);
        border-radius: 18px;
        padding: 32px 40px 24px 40px;
        box-sizing: border-box;
        border: none;
      "
      body-style="padding:0;"
    >
      <el-button
        type="primary"
        style="margin: 18px 0 18px 0; width: 200px; font-weight: bold; letter-spacing: 2px; border-radius: 8px; box-shadow: 0 2px 8px 0 rgba(64,158,255,0.08);"
        @click="openDialog('add')"
      >
        新增部门
      </el-button>
      <el-tree
        :data="deptTree"
        :props="treeProps"
        highlight-current
        node-key="id"
        @node-click="handleDepartmentClick"
        style="width: 100%; font-size: 17px;"
        class="dept-tree-custom"
        default-expand-all
      >
        <template #default="{ node, data }">
          <span style="display: flex; align-items: center; justify-content: space-between; width: 100%; padding: 6px 0;">
            <span style="font-weight: 600; color: #222;">{{ data.name }}</span>
            <span style="display: flex; gap: 4px;">
              <el-button size="small" type="success" text style="padding: 0 6px;" @click.stop="openDialog('edit', data)">编辑</el-button>
              <el-button size="small" type="danger" text style="padding: 0 6px;" @click.stop="handleDelete(data)">删除</el-button>
            </span>
          </span>
        </template>
      </el-tree>
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑部门' : '新增部门'" width="400px">
      <div v-if="isEdit && formData.name" style="margin-bottom: 10px; color: #888;">
        当前部门：{{ formData.name }}
      </div>
      <div v-else-if="formData.parentId > 0" style="margin-bottom: 10px; color: #888;">
        当前父级部门：{{ getDeptNameById(formData.parentId) }}
      </div>
      <el-form :model="formData" label-width="100px">
        <el-form-item label="部门名称">
          <el-input v-model="formData.name" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="父级部门">
          <el-cascader
            v-model="formData.parentId"
            :options="parentTreeOptions"
            :props="{ checkStrictly: true, emitPath: false }"
            clearable
            placeholder="请选择父级部门"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">{{ isEdit ? '保存' : '创建' }}</el-button>
      </template>
    </el-dialog>
    <el-dialog v-model="detailDialogVisible" title="部门详情" width="600px" :show-close="true">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="ID">{{ detailData?.department?.id }}</el-descriptions-item>
        <el-descriptions-item label="名称">{{ detailData?.department?.name }}</el-descriptions-item>
        <el-descriptions-item label="父级ID">{{ detailData?.department?.parentId }}</el-descriptions-item>
      </el-descriptions>
      <div style="margin: 16px 0 8px 0; font-weight: bold;">老师列表</div>
      <el-table :data="detailData?.teachers || []" size="small" border style="margin-bottom: 16px;">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="工号" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="80" />
      </el-table>
      <div style="margin: 8px 0; font-weight: bold;">学生列表</div>
      <el-table :data="detailData?.students || []" size="small" border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="学号" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="80" />
        <el-table-column prop="gpa" label="GPA" width="80" />
      </el-table>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </ContentWrap>
</template>

<style scoped>
.dept-tree-custom .el-tree-node__content:hover {
  background: #e6f7ff;
  border-radius: 8px;
}
</style>
