<template>
  <div>
    <h2 style="margin-bottom: 20px">教学任务管理</h2>
    <!-- 筛选表单 -->
    <el-form :inline="true" :model="searchForm" class="mb-20px">
      <el-form-item label="任务名称">
        <el-input v-model="searchForm.taskName" placeholder="请输入任务名称" />
      </el-form-item>
      <el-form-item label="班级">
        <el-input v-model="searchForm.className" placeholder="请输入班级" />
      </el-form-item>
      <el-form-item label="教师ID">
        <el-input v-model="searchForm.teacherId" placeholder="请输入教师ID" style="width: 150px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 按钮区 -->
    <div class="mb-10px">
      <el-button type="primary">新 增</el-button>
      <el-button type="success">修改</el-button>
      <el-button type="danger">删除</el-button>
      <el-button type="warning">导出</el-button>
    </div>

    <!-- 表格 -->
    <el-table :data="taskList" border style="width: 100%">
      <el-table-column type="selection" width="50" />
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="taskName" label="任务名称" min-width="200" />
      <el-table-column prop="className" label="班级" width="120" />
      <el-table-column prop="teacherId" label="教师ID" width="100" />
      <el-table-column label="操作" width="120">
        <template #default="scope">
          <el-link type="primary" @click="editTask(scope.row)">修改</el-link>
          <el-link type="danger" @click="deleteTask(scope.row)" style="margin-left: 10px">
            删除
          </el-link>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const searchForm = ref({
  taskName: '',
  className: '',
  teacherId: ''
})

const taskList = ref([
  {
    id: 1,
    taskName: '2022-2023学年春季Python与数据分析一',
    className: '软件2201',
    teacherId: 1
  },
  {
    id: 2,
    taskName: '2022-2023学年春季Python与数据分析二',
    className: '软件2202',
    teacherId: 2
  },
  {
    id: 3,
    taskName: '2022-2023学年秋季自然语言处理一',
    className: '软件2203',
    teacherId: 1
  },
  {
    id: 4,
    taskName: '2022-2023学年秋季自然语言处理二',
    className: '软件2204',
    teacherId: 2
  },
  {
    id: 5,
    taskName: '2024-2025学年春季工程伦理学',
    className: '软件2205',
    teacherId: 3
  },
  {
    id: 7,
    taskName: '2027-2028学年春季Python与数据分析一',
    className: '软件2307',
    teacherId: 7
  }
])

function handleSearch() {
  // 这里可以添加实际的搜索逻辑
}

function handleReset() {
  searchForm.value = { taskName: '', className: '', teacherId: '' }
}

function editTask(row) {
  // 编辑任务逻辑
  alert('编辑任务：' + row.taskName)
}

function deleteTask(row) {
  // 删除任务逻辑
  alert('删除任务：' + row.taskName)
}
</script>

<style scoped>
.mb-10px {
  margin-bottom: 10px;
}
.mb-20px {
  margin-bottom: 20px;
}
</style>
