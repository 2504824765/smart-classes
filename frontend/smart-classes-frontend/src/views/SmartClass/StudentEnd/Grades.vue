<template>
  <div class="student-grade-container">
    <el-row justify="center">
      <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
        <el-card>
          <h2 style="text-align: center;">学生成绩查看</h2>
          <p style="text-align: center; margin-bottom: 20px;">
            学生姓名: {{ student.name }}（ID: {{ student.id }}）
          </p>

          <el-table :data="grades" stripe border style="width: 100%; margin-top: 20px">
            <el-table-column prop="name" label="课程名称" />
            <el-table-column prop="credit" label="学分" />
            <el-table-column prop="class_hours" label="学时" />
            <el-table-column prop="grade" label="成绩" />
          </el-table>

          <div style="margin-top: 30px; font-weight: bold; font-size: 18px; text-align: center;">
            综合绩点 GPA: {{ gpa.toFixed(2) }}
          </div>
        </el-card>
      </el-col>
    </el-row>
    

  </div>
</template>

<script setup lang="ts">
import axios from 'axios'
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/store/modules/user'
import { getStudentByUsernameApi, getStudentByIdApi } from '@/api/student/index'
import { getClassesByIdApi } from '@/api/classes/index'
import { getAssociatedBySidApi } from '@/api/studentClasses/index'


const studentId = ref<number | null>(null)

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

const studentName = ref<string | null>(null)
const student = ref({
  id: studentId,
  name: studentName
})
const grades = ref([])

// 获取学生姓名
const fetchStudentInfo = async () => {
  try {
    const res = await getStudentByIdApi(student.value.id)
    studentName.value = res.data.name
  } catch (error) {
    console.error('获取学生信息失败:', error)
  }
}

// 获取学生的选课记录
const fetchStudentGrades = async () => {
  try {
    const res = await getAssociatedBySidApi(student.value.id)
    console.log('获取到的 res：', res)
    const associatedList = Array.isArray(res.data) ? res.data: []
    console.log('提取到的 associatedList：', associatedList)

    if (associatedList.length === 0) {
      console.warn('该学生没有选课记录')
      grades.value = []
      return
    }

    grades.value = associatedList.map((item: any) => {
      const classData = item.classes
      return {
        name: classData?.name || '未知课程',
        credit: Number(classData?.credit || 0),
        class_hours: Number(classData?.classHours || 0),
        grade: Number(item.grade || 0)
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
      const gradePoint = Math.max((cur.grade - 50) / 10, 0) // 按照你的规则换算 GPA
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
.student-grade-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}
</style>