<template>
  <div class="file-display flex items-center gap-4 p-4 bg-white rounded shadow">
    <component :is="iconComponent" class="text-3xl text-gray-500" />
    <div class="text-sm text-gray-700 break-all">
      {{ filename }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { FilePdfOutlined, FileImageOutlined, FileWordOutlined, VideoCameraOutlined, FileUnknownOutlined } from '@vicons/antd'

const props = defineProps<{
  url: string
}>()

const filename = computed(() => {
  try {
    return decodeURIComponent(props.url.split('/').pop() || '未知文件')
  } catch {
    return '未知文件'
  }
})

const fileType = computed(() => {
  const ext = props.url.split('.').pop()?.toLowerCase() || ''
  if (['png', 'jpg', 'jpeg', 'gif', 'bmp', 'webp'].includes(ext)) return 'image'
  if (['mp4', 'webm', 'avi', 'mov'].includes(ext)) return 'video'
  if (['pdf'].includes(ext)) return 'pdf'
  if (['doc', 'docx'].includes(ext)) return 'word'
  return 'unknown'
})

const iconComponent = computed(() => {
  switch (fileType.value) {
    case 'pdf':
      return FilePdfOutlined
    case 'word':
      return FileWordOutlined
    case 'image':
      return FileImageOutlined
    case 'video':
      return VideoCameraOutlined
    default:
      return FileUnknownOutlined
  }
})
</script>

<style scoped>
.file-display {
  border: 1px solid #e5e7eb;
}
</style>
