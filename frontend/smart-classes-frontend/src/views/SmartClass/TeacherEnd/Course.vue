<template>
  <div class="flex items-center mb-4 gap-4">
    <el-input v-model="searchKeyword" placeholder="搜索课程名" clearable />
    <el-switch v-model="onlyShowActive" active-text="仅显示启用课程" />
  </div>

  <draggable v-model="displayList" item-key="id" class="course-list" animation="200">
    <template #item="{ element }">
      <CourseCard :course="element" :key="element.id" :disabled="!element.active" />
    </template>
  </draggable>
</template>

<script setup lang="ts">
import CourseCard from './components/CourseCard.vue'
import { Classes } from '@/api/classes/types'
import { getAllClassesApi } from '@/api/classes/index'
import { onMounted, ref, computed, watch } from 'vue'
import draggable from 'vuedraggable'

const courseList = ref<Classes[]>([])

const queryCourseList = async () => {
  const res = await getAllClassesApi()
  courseList.value = res.data
}

const searchKeyword = ref('')
const onlyShowActive = ref(false)

const displayList = ref<Classes[]>([])

watch([courseList, searchKeyword, onlyShowActive], () => {
  displayList.value = courseList.value.filter((course) => {
    const matchKeyword = course.name?.includes(searchKeyword.value)
    const matchActive = onlyShowActive.value ? course.active : true
    return matchKeyword && matchActive
  })
})

onMounted(() => {
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
