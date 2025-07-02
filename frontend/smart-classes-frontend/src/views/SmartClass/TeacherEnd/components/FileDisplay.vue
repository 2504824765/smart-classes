<template>
  <el-card
    class="file-display"
    shadow="hover"
    :body-style="{ display: 'flex', alignItems: 'center', gap: '1rem', padding: '1rem' }"
  >
    <el-link :href="props.url" target="_blank" :underline="false" class="file-link">
      <component :is="iconComponent" class="file-icon" />
      <span class="file-name">{{ filename }}</span>
    </el-link>
  </el-card>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import {
  FilePdfOutlined,
  FileImageOutlined,
  FileWordOutlined,
  VideoCameraOutlined,
  FileUnknownOutlined
} from '@vicons/antd'

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
  border-radius: 8px;
  transition: box-shadow 0.2s ease;
  width: 100%; /* 或固定宽度也可 */
}

.file-link {
  display: flex;
  align-items: center;
  justify-content: space-between;
  text-decoration: none;
  color: inherit;
  width: 100%;
}

.file-icon {
  font-size: 32px;
  color: #6b7280;
  flex-shrink: 0;
  width: 32px;
  height: 32px;
}

.file-name {
  font-size: 14px;
  line-height: 1.4;
  color: #374151;
  max-width: 220px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  text-overflow: ellipsis;
}
</style>
