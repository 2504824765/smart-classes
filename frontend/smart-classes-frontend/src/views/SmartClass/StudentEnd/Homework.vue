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
import { Classes, CourseDisplayData } from '@/api/classes/classes'
import { ref } from 'vue'
const { push } = useRouter()

const classes = ref<Classes[]>([])

const courses = ref<CourseDisplayData[]>([
  {
    name: '高等数学',
    image: '/default.png',
    description: '函数、极限、积分等知识',
    unfinished: 2,
    total: 5
  },
  {
    name: '大学英语',
    image: '/default.png',
    description: '阅读与写作训练',
    unfinished: 1,
    total: 3
  }
])

const displayCourses: CourseDisplayData[] = classes.value.map(cls => {
  const stats = courses[cls.id] || { unfinished: 0, total: 0 }

  return {
    name: cls.name,
    image: cls.image || '/default.png',
    description: cls.description,
    unfinished: stats.unfinished,
    total: stats.total
  }
})

function goToHomework(course: any) {
  push({ path: '/homework/list', query: { course: course.name } })
}
</script>

<style scoped>
.course-list {
  padding: 20px;
}
</style>
