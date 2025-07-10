<template>
  <ContentWrap>
    <el-card shadow="hover" class="grade-card">
      <h2 class="title">学生成绩查看</h2>
      <p class="student-info">
        学生姓名: {{ student.name }}（ID: {{ student.id }}）
      </p>

      <el-table
        :data="grades"
        stripe
        border
        class="grade-table"
      >
        <el-table-column prop="name" label="课程名称" min-width="240" align="center" />
        <el-table-column prop="credit" label="学分" width="100" align="center" />
        <el-table-column prop="class_hours" label="学时" width="100" align="center" />
        <el-table-column prop="grade" label="成绩" width="100" align="center" />
      </el-table>

      <div class="gpa-info">
        综合绩点 GPA: <span class="gpa-value">{{ gpa.toFixed(2) }}</span>
      </div>
    </el-card>
  </ContentWrap>
</template>


<script setup lang="ts">

import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/store/modules/user'
import { getStudentByUsernameApi, getStudentByIdApi } from '@/api/student/index'
import { getAssociatedBySidApi } from '@/api/studentClasses/index'


interface GradeItem {
  name: string
  credit: number
  class_hours: number
  grade: number
}

const student = ref({
  id: null as number | null,
  name: ''
})

const studentId = ref<number | null>(null)
const grades = ref<GradeItem[]>([])

const getStudentId = async (username: string) => {
  const res = await getStudentByUsernameApi(username)
  studentId.value = res.data.id
  console.log(studentId.value)
}

const userStore = useUserStore()
const loginInfo = userStore.getLoginInfo
const initialize = async () => {
  if (loginInfo && loginInfo.username) {
    await getStudentId(loginInfo.username)

    if (studentId.value) {
      student.value.id = studentId.value

      await fetchStudentInfo()
      await fetchStudentGrades()
    } else {
      console.error('未能获取 studentId')
    }
  }
}

// 获取学生姓名
const fetchStudentInfo = async () => {
  if (!student.value.id) {
    console.error('学生ID为空')
    return
  }
  try {
    const res = await getStudentByIdApi(student.value.id)
    student.value.name = res.data.name
  } catch (error) {
    console.error('获取学生信息失败:', error)
  }
}

// 获取学生的选课记录
const fetchStudentGrades = async () => {
    if (!student.value.id) {
      console.error('学生ID为空')
      return
  }
  try {
    const res = await getAssociatedBySidApi(student.value.id)
    // console.log('获取到的 res：', res)
    const associatedList = Array.isArray(res.data) ? res.data: []
    // console.log('提取到的 associatedList：', associatedList)

    if (associatedList.length === 0) {
      console.warn('该学生没有选课记录')
      grades.value = []
      return
    }

    grades.value = associatedList
      .filter((item: any) => item.grade !== null && item.grade !== undefined && item.grade !== '' && !isNaN(Number(item.grade)))
      .map((item: any) => {
        const classData = item.classes
        return {
          name: classData?.name || '未知课程',
          credit: Number(classData?.credit || 0),
          class_hours: Number(classData?.classHours || 0),
          grade: Number(item.grade)
        }
      })

    console.log('成绩数据：', grades.value)

  } catch (error) {
    console.error('获取选课或课程信息失败:', error)
  }
}


// GPA
const gpa = computed(() => {
  if (grades.value.length === 0) return 0

  const total = grades.value.reduce(
    (acc, cur) => {
      const gradePoint = Math.max((cur.grade - 50) / 10, 0)
      acc.totalGpa += gradePoint * cur.credit
      acc.totalCredit += cur.credit
      return acc
    },
    { totalGpa: 0, totalCredit: 0 }
  )

  return total.totalCredit > 0 ? total.totalGpa / total.totalCredit : 0
})


onMounted(async () => {
  await initialize()
    console.log('成绩数据：', grades.value)
})
</script>



<style scoped>
.grade-card {
  background: #fff;
  border: 1px solid #e4e7ed;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  padding: 30px;
}

.title {
  text-align: center;
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 10px;
}

.student-info {
  text-align: center;
  margin-bottom: 20px;
  font-size: 16px;
  color: #666;
}

.grade-table {
  margin-top: 20px;
}

.gpa-info {
  margin-top: 30px;
  font-size: 18px;
  font-weight: bold;
  text-align: center;
  color: #333;
}

.gpa-value {
  color: #409eff;
  margin-left: 8px;
}


</style>