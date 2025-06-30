<template>
  <div class="p-4 bg-white rounded shadow">
    <p>{{ content }}</p>
  </div>
</template>

<script setup lang="ts">
defineProps<{ content: string }>()
</script>