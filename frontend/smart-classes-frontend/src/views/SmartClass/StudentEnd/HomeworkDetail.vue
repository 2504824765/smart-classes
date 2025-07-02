<template>
  <div class="homework-detail">
    <el-page-header content="作业详情" @back="router.back" />
    <el-card>
      <h2>{{ route.query.title }}</h2>
      <p>这里将展示作业的详细要求、上传入口等内容（待开发）。</p>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
</script>

<style scoped>
.homework-detail {
  padding: 20px;
}
</style>
