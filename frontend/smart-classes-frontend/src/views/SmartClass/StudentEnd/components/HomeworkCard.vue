<template>
  <el-card class="homework-card" shadow="hover" @click="$emit('view-detail', homework)">
    <div class="card-header">
      <div class="title">{{
        homework.classMission.id ? `任务 # ${homework.classMission.type} ` : '未知任务'
      }}</div>
      <el-tag
        :type="homework.done ? 'success' : homework.active ? 'warning' : 'danger'"
        size="small"
      >
        {{ homework.done ? '已完成' : homework.active ? '未完成' : '已截止' }}
      </el-tag>
    </div>
    <p class="brief">成绩：{{ homework.score ?? '未评分' }}</p>
  </el-card>
</template>

<script setup lang="ts">
import type { StudentMission } from '@/api/studentMission/types' // 根据路径调整

const props = defineProps<{
  homework: StudentMission
}>()
console.log(props.homework)

defineEmits(['view-detail'])
</script>

<style scoped>
.homework-card {
  width: 300px;
  height: 160px;
  margin: 20px;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-weight: bold;
  font-size: 16px;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 简介文本限制两行显示 */
.brief {
  margin-top: 10px;
  color: #666;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2; /* 显示两行 */
  -webkit-box-orient: vertical;
}
</style>
