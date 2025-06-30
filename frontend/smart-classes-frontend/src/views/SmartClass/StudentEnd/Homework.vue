<template>
  <div class="course-list">
    <el-row>
      <el-col :span="6" v-for="course in courses" :key="course.name">
        <CourseHCard :course="course" @view-homework="goToHomework(course)" />
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import CourseHCard from './components/CourseHCard.vue'
import { useRouter } from 'vue-router'

const { push } = useRouter()

const courses = [
  {
    name: '高等数学',
    image: '',
    description: '函数、极限、积分等知识',
    unfinished: 2,
    total: 5
  },
  {
    name: '大学英语',
    image: '',
    description: '阅读与写作训练',
    unfinished: 1,
    total: 3
  }
]

function goToHomework(course: any) {
  push({ path: '/homework/list', query: { course: course.name } })
}
</script>

<style scoped>
.course-list {
  padding: 20px;
}
</style>
