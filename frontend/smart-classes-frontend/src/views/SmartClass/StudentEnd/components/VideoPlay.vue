<script setup lang="ts">
import Player from 'xgplayer'
import { ref, unref, onMounted, watch, onBeforeUnmount, nextTick } from 'vue'
import 'xgplayer/dist/index.min.css'

const props = defineProps({
  url: {
    type: String,
    default: '',
    required: true
  },
  poster: {
    type: String,
    default: ''
  }
})

const playerRef = ref<Player>()

const videoEl = ref<HTMLDivElement>()

const intiPlayer = () => {
  if (!unref(videoEl)) return
  playerRef.value = new Player({
    autoplay: false,
    ...props,
    el: unref(videoEl)
  })
}

onMounted(() => {
  intiPlayer()
})

watch(
  () => props,
  async (newProps) => {
    await nextTick()
    if (newProps) {
      unref(playerRef)?.setConfig(newProps)
    }
  },
  {
    deep: true
  }
)

defineExpose({
  playerExpose: () => unref(playerRef)
})
</script>

<template>
  <div class="video-container">
    <div ref="videoEl"></div>
  </div>
</template>

<style scoped>
.video-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
  width: 100%;
}

.video-container > div {
  max-width: 800px;
  width: 100%;
}
</style>
