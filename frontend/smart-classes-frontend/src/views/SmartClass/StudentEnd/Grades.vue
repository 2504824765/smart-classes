<template>
  <div class="student-grade-container">
    <el-row justify="center">
      <el-col :xs="24" :sm="20" :md="18" :lg="16" :xl="14">
        <el-card>
          <h2>学生成绩查看</h2>
          <p>学生姓名: {{ student.name }}（ID: {{ student.id }}）</p>

          <el-table :data="grades" stripe border style="width: 100%; margin-top: 20px">
            <el-table-column prop="name" label="课程名称" />
            <el-table-column prop="credit" label="学分" />
            <el-table-column prop="class_hours" label="学时" />
            <el-table-column prop="grade" label="成绩" />
          </el-table>

          <div style="margin-top: 20px; font-weight: bold">
            综合绩点 GPA: {{ gpa.toFixed(2) }}
          </div>
        </el-card>
      </el-col>
    </el-row>

  </div>
</template>

<script setup>
import axios from 'axios'
import { ref, computed, onMounted } from 'vue'

// 假设登录时将 studentId 存入 localStorage
const studentId = localStorage.getItem('studentId') || 1

// 保存学生信息
const student = ref({
  id: Number(studentId),
  name: '',
  gpa: null
})

// 成绩列表
const grades = ref([])

// 获取学生成绩数据（从 studentClasses 表）
const fetchStudentGrades = async () => {
  try {
    const res = await axios.get(`/api/studentClasses?studentId=${student.value.id}`)
    const studentClassList = res.data

    // 映射出前端需要展示的格式
    grades.value = studentClassList.map(item => ({
      name: item.classes.name,
      credit: item.classes.credit,
      class_hours: item.classes.classHours,
      grade: item.grade
    }))

    // 也可以顺带设置学生姓名
    if (studentClassList.length > 0) {
      student.value.name = studentClassList[0].student.name
    }

  } catch (error) {
    console.error('获取成绩失败:', error)
  }
}

// GPA 计算（成绩×学分 加权平均）
const gpa = computed(() => {
  if (grades.value.length === 0) return 0
  const total = grades.value.reduce((acc, cur) => {
    acc.totalGrade += cur.grade * cur.credit
    acc.totalCredit += cur.credit
    return acc
  }, { totalGrade: 0, totalCredit: 0 })
  return total.totalCredit > 0 ? total.totalGrade / total.totalCredit : 0
})

onMounted(() => {
  fetchStudentGrades()
})
</script>


<style scoped>
.student-grade-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}
</style>