<template>
  <el-card class="homework-card" shadow="hover" @click="$emit('view-detail', homework)">
    <div class="card-header">
      <h4>{{ homework.title }}</h4>
      <el-tag :type="homework.finished ? 'success' : 'danger'">
        {{ homework.finished ? '已完成' : '未完成' }}
      </el-tag>
    </div>
    <p>{{ homework.brief }}</p>
  </el-card>
</template>

<script setup lang="ts">
defineProps<{
  homework: {
    title: string
    brief: string
    finished: boolean
  }
}>()
defineEmits(['view-detail'])
</script>

<style scoped>
.homework-card {
  width: 300px;
  margin: 20px;
  cursor: pointer;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
