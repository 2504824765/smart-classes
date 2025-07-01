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

// 登录后从localStorage拿studentId
const studentId = Number(localStorage.getItem('studentId') || 0)

const student = ref({
  id: studentId,
  name: ''
})

const grades = ref([])

// 获取学生姓名
const fetchStudentInfo = async () => {
  try {
    const res = await axios.get(`/api/student/getStudentById/${student.value.id}`)
    student.value.name = res.data.name
  } catch (error) {
    console.error('获取学生信息失败:', error)
  }
}

// 获取学生的选课记录
const fetchStudentGrades = async () => {
  try {
    const res = await axios.get(`/api/scAssociated/getAssociatedBySid/${student.value.id}`)
    const associatedList = res.data.data

    // 对每个课程ID，请求classes信息
    const classRequests = associatedList.map(async (item) => {
      const classRes = await axios.get(`/api/class/getClassById/${item.cid}`)
      const classData = classRes.data
      return {
        name: classData.name,
        credit: classData.credit,
        class_hours: classData.classHours,
        grade: item.grade
      }
    })

    // 等待所有课程信息加载完成
    grades.value = await Promise.all(classRequests)
  } catch (error) {
    console.error('获取选课或课程信息失败:', error)
  }
}

// GPA
const gpa = computed(() => {
  if (grades.value.length === 0) return 0
  const total = grades.value.reduce((acc, cur) => {
    acc.totalGrade += cur.grade * cur.credit
    acc.totalCredit += cur.credit
    return acc
  }, { totalGrade: 0, totalCredit: 0 })
  return total.totalCredit > 0 ? total.totalGrade / total.totalCredit : 0
})

onMounted(async () => {
  await fetchStudentInfo()
  await fetchStudentGrades()
})
</script>



<style scoped>
.student-grade-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}
</style>