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
const detailData = ref<Department | null>(null)

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

const openDialog = (type: 'add' | 'edit', node?: Department) => {
  isEdit.value = type === 'edit'
  if (isEdit.value && node) {
    Object.assign(formData, node)
    formData.parentId = node.parentId
  } else {
    Object.assign(formData, { id: null, name: '', parentId: 0 })
    if (node) {
      formData.parentId = node.id
    }
  }
  // 构建父级下拉选项为树结构
  parentTreeOptions.value = listToTree(deptTree.value)
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
    const { id, ...createData } = formData
    await addDepartmentApi(createData)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  await fetchDepartmentTree()
  tableMethods.refresh()
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
      <el-tree
        :data="deptTree"
        :props="treeProps"
        highlight-current
        node-key="id"
        @node-click="handleDepartmentClick"
        style="width: 100%; font-size: 17px;"
        class="dept-tree-custom"
      >
        <template #default="{ node, data }">
          <span style="display: flex; align-items: center; justify-content: space-between; width: 100%; padding: 6px 0;">
            <span style="font-weight: 600; color: #222;">{{ data.name }}</span>
            <span style="display: flex; gap: 4px;">
              <el-button size="small" type="primary" text style="padding: 0 6px;" @click.stop="openDialog('add', data)">新增</el-button>
              <el-button size="small" type="success" text style="padding: 0 6px;" @click.stop="openDialog('edit', data)">编辑</el-button>
              <el-button size="small" type="danger" text style="padding: 0 6px;" @click.stop="handleDelete(data)">删除</el-button>
            </span>
          </span>
        </template>
      </el-tree>
      <el-button
        type="primary"
        style="margin-top: 18px; width: 100%; font-weight: bold; letter-spacing: 2px; border-radius: 8px; box-shadow: 0 2px 8px 0 rgba(64,158,255,0.08);"
        @click="openDialog('add')"
      >
        新增顶级部门
      </el-button>
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑部门' : '新增部门'" width="400px">
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
    <el-dialog v-model="detailDialogVisible" title="部门详情" width="400px" :show-close="true">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="ID">{{ detailData?.id }}</el-descriptions-item>
        <el-descriptions-item label="名称">{{ detailData?.name }}</el-descriptions-item>
        <el-descriptions-item label="父级ID">{{ detailData?.parentId }}</el-descriptions-item>
      </el-descriptions>
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
