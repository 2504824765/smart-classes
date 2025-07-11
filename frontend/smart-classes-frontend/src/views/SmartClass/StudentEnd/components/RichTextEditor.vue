<template>
  <div>
    <div ref="toolbarRef" style="border: 1px solid #ccc; margin-bottom: 10px"></div>
    <div ref="editorRef" style="border: 1px solid #ccc; min-height: 300px"></div>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, ref, onBeforeUnmount, watch } from 'vue'
import {
  IDomEditor,
  IEditorConfig,
  IToolbarConfig,
  createEditor,
  createToolbar,
  Boot
} from '@wangeditor/editor'
import '@wangeditor/editor/dist/css/style.css'

const props = defineProps<{ modelValue: string }>()
const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
}>()

const editorRef = ref<HTMLDivElement | null>(null)
const toolbarRef = ref<HTMLDivElement | null>(null)

let editor: IDomEditor | null = null

onMounted(() => {
  const editorConfig: Partial<IEditorConfig> = {
    placeholder: '请输入内容...',
    onChange(editor) {
      emit('update:modelValue', editor.getHtml())
    }
  }

  const toolbarConfig: Partial<IToolbarConfig> = {
    excludeKeys: ['group-video'] // 可选：排除某些工具栏项
  }

  editor = createEditor({
    selector: editorRef.value!,
    config: editorConfig,
    mode: 'default'
  })

  if (props.modelValue) {
    editor.setHtml(props.modelValue)
  }

  createToolbar({
    editor,
    selector: toolbarRef.value!,
    config: toolbarConfig,
    mode: 'default'
  })
})

watch(
  () => props.modelValue,
  (newVal) => {
    if (editor && newVal !== editor.getHtml()) {
      editor.setHtml(newVal)
    }
  }
)

onBeforeUnmount(() => {
  editor?.destroy()
})
</script>
