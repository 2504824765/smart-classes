<template>
  <div class="flex items-center mb-4 gap-4">
    <el-input v-model="searchKeyword" placeholder="搜索课程名" clearable />
    <el-switch v-model="onlyShowActive" active-text="仅显示启用课程" />
  </div>

  <el-empty v-if="displayList.length === 0">暂无课程</el-empty>

  <draggable v-model="displayList" item-key="id" class="course-list" animation="200">
    <template #item="{ element }">
      <CourseCard :course="element" :key="element.id" :disabled="!element.isActive" />
    </template>
  </draggable>
</template>

<script setup lang="ts">
import CourseCard from './components/CourseCard.vue'
import { Classes } from '@/api/classes/types'
import { getAllClassesApi } from '@/api/classes/index'
import { onMounted, ref, watch } from 'vue'
import { getTeacherByUsernameApi } from '@/api/teacher'
import { useUserStore } from '@/store/modules/user'
import draggable from 'vuedraggable'

const teacherId = ref<number>()
const getId = async (username: string) => {
  const res = await getTeacherByUsernameApi(username)
  teacherId.value = res.data.id
}

const userStore = useUserStore()
const loginInfo = userStore.getLoginInfo
const initialize = async () => {
  if (loginInfo) {
    const username = loginInfo.username
    await getId(username)
  }
}

const courseList = ref<Classes[]>([])

const queryCourseList = async () => {
  const res = await getAllClassesApi()
  console.log(res)
  courseList.value = res.data.filter((course: Classes) => course.teacher.id === teacherId.value)
}

const searchKeyword = ref('')
const onlyShowActive = ref(false)

const displayList = ref<Classes[]>([])

watch([courseList, searchKeyword, onlyShowActive], () => {
  displayList.value = courseList.value.filter((course) => {
    const matchKeyword = course.name?.includes(searchKeyword.value)
    const matchActive = onlyShowActive.value ? course.isActive : true
    return matchKeyword && matchActive
  })
})

onMounted(() => {
  initialize()
  queryCourseList()
})
</script>

<style scoped>
.course-list {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
  min-height: 300px;
}
</style>
