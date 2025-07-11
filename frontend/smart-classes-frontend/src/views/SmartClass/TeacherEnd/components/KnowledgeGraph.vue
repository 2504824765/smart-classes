<template>
  <div class="grid grid-cols-12 gap-3 h-[100vh]">
    <el-card class="col-span-3 overflow-auto">
      <el-tree :data="treeData" :props="defaultProps" />
    </el-card>

    <el-card
      v-loading="loading"
      :element-loading-text="`图谱正在生成，请耐心等待... ${progressData}%`"
      class="col-span-9 flex flex-col"
    >
      <el-tabs v-model="activeTab" class="flex-1" tab-position="top">
        <!-- 图谱 tab -->
        <el-tab-pane label="图谱" name="graph">
          <div class="flex items-center justify-between mb-2">
            <div class="flex items-center">
              <ElButton type="primary" @click="createKnowledgeGraph" :disabled="hasGraph"
                >生成图谱</ElButton
              >
              <ElButton
                type="primary"
                class="ml-2"
                @click="deleteKnowledgeGraph"
                :disabled="!hasGraph"
                >删除图谱</ElButton
              >
            </div>
          </div>
          <div
            ref="graphContainer"
            class="w-full h-[calc(80vh-120px)]"
            style="min-height: 500px"
          ></div>
        </el-tab-pane>

        <!-- 资源 tab -->
        <el-tab-pane label="资源文件" name="files">
          <div class="overflow-auto h-[calc(80vh-120px)] px-2 pt-2">
            <el-empty v-if="fileCards.length === 0" description="暂无资源" />
            <div v-for="file in fileCards" :key="file.name" class="mb-2">
              <FileDisplay :url="file.url" />
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import G6 from '@antv/g6'
import FileDisplay from './FileDisplay.vue'
import { getResourceByClassIdApi } from '@/api/resource/index'
import type { Resource } from '@/api/resource/types'
import { getClassesByIdApi, updateClassApi } from '@/api/classes'
import { PREFIX } from '@/constants/index'
import { createGraphApi } from '@/api/dify/index'
import { createDifyGraphRequestMulti } from '@/api/dify/types'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ClassesUpdateDTO } from '@/api/classes/types'

const props = defineProps<{ classId: number | undefined }>()

const graphContainer = ref<HTMLDivElement>()

const treeData = ref<any[]>([])
const hasGraph = computed(() => treeData.value.length > 0)
const activeTab = ref('graph')
const defaultProps = {
  children: 'children',
  label: 'label'
}

const fileCards = ref([
  {
    name: '神经网络.pdf',
    type: 'PDF',
    url: 'https://smart-class-northeast.oss-cn-beijing.aliyuncs.com/user-images/43d2ef69-83d1-44ac-9234-a790cb99dec0.jpg'
  },
  {
    name: '深度学习.pptx',
    type: 'PPTX',
    url: 'https://smart-class-northeast.oss-cn-beijing.aliyuncs.com/class/web开发技术/json/web开发技术知识图谱.json'
  }
])

const loading = ref(false)
const progressData = ref(0) // 进度百分比

let timer: number | null = null

// 监听loading变化，开始或停止进度计时
watch(loading, (val) => {
  if (val) {
    progressData.value = 0
    timer = window.setInterval(() => {
      if (progressData.value < 99) {
        progressData.value += 1
      }
    }, 2000) // 每2秒加1%
  } else {
    progressData.value = 100
    if (timer) {
      clearInterval(timer)
      timer = null
    }
  }
})

const registerCustomNode = () => {
  G6.registerNode(
    'progress-node',
    {
      draw(cfg, group) {
        const size = (Array.isArray(cfg.size) ? cfg.size[0] : cfg.size) || 30
        const label = cfg.label || ''
        const style = cfg.style || {}
        const data = (cfg.data || {}) as { progress?: number }
        const progress = data.progress ?? 0

        const radius = size / 2 - 4
        const circumference = 2 * Math.PI * radius

        const keyShape = group.addShape('circle', {
          attrs: {
            r: size / 2,
            fill: style.fill || '#fff',
            stroke: style.stroke || '#5B8FF9',
            lineWidth: 2
          },
          name: 'main-circle',
          draggable: true
        })

        group.addShape('circle', {
          attrs: {
            r: radius,
            stroke: '#f0f0f0',
            lineWidth: 4
          },
          name: 'bg-bar'
        })

        group.addShape('circle', {
          attrs: {
            r: radius,
            stroke: '#6EDD87',
            lineWidth: 4,
            lineDash: [circumference * progress, circumference * (1 - progress)],
            lineDashOffset: -circumference / 4
          },
          name: 'progress-bar'
        })

        group.addShape('text', {
          attrs: {
            x: 0,
            y: size / 2 + 10,
            text: label || '',
            fontSize: 12,
            fill: '#333',
            textAlign: 'center',
            textBaseline: 'top'
          },
          name: 'node-label'
        })

        return keyShape
      },
      setState(name, value, item) {
        if (!item) return
        const group = item.getContainer()
        const keyShape = item.getKeyShape()

        if (name === 'hover') {
          keyShape.attr({
            shadowColor: value ? '#1890ff' : null,
            shadowBlur: value ? 20 : 0,
            lineWidth: value ? 3 : 1,
            size: value ? 60 : 50
          })

          // 控制标签显示
          const label = group.find((el) => el.cfg.name === 'node-label')
          if (label) label.set('visible', value)
        }

        if (name === 'dimmed') {
          keyShape.attr({
            opacity: value ? 0.2 : 1
          })

          const label = group.find((el) => el.cfg.name === 'node-label')
          if (label) label.set('visible', !value)
        }
      }
    },
    'circle'
  )
}

const initGraph = async () => {
  if (!props.classId) {
    console.warn('classId 不存在，跳过图谱加载')
    return
  }

  if (!graphContainer.value) {
    console.error('图谱容器未找到')
    return
  }
  await registerCustomNode()

  // 获取容器尺寸，如果为0则设置默认值
  const container = graphContainer.value
  const containerWidth = container.clientWidth || 800
  const containerHeight = container.clientHeight || 600

  if (containerWidth === 0 || containerHeight === 0) {
    console.warn('容器尺寸为0，延迟初始化')
    setTimeout(() => initGraph(), 200)
    return
  }

  const nodes: any[] = []
  const edges: any[] = []
  const rootNode = ref<any>()

  const walkTreeList = (treeList: any[], parentId: string | null = null) => {
    for (const node of treeList) {
      nodes.push({
        id: node.id,
        label: node.label,
        type: 'progress-node',
        progress: node.progress ?? Math.random(),
        style: {
          fill: parentId ? '#4BABF4' : '#5B8FF9',
          stroke: '#5B8FF9'
        }
      })

      if (parentId) {
        rootNode.value = node
      }

      if (parentId) {
        edges.push({
          source: parentId,
          target: node.id
        })
      }

      if (node.children && Array.isArray(node.children)) {
        walkTreeList(node.children, node.id)
      }
    }
  }

  walkTreeList(treeData.value)
  const graph = new G6.Graph({
    container: graphContainer.value!,
    width: graphContainer.value!.offsetWidth,
    height: graphContainer.value!.offsetHeight,
    layout: {
      type: 'force',
      center: [containerWidth / 2, containerHeight / 2],
      linkDistance: 10,
      maxIteration: 1000,
      focusNode: rootNode.value?.id,
      unitRadius: 120,
      preventOverlap: true,
      nodeSize: 80,
      nodeSpacing: 10, // 与 size 一起配合，给每个节点外留空间
      strictRadial: false
    },
    modes: {
      default: ['drag-canvas', 'drag-node']
    },
    // 适应画布
    fitView: true,
    fitViewPadding: 20,
    defaultNode: {
      size: 45,
      labelCfg: {
        style: {
          opacity: 0
        }
      }
    },
    defaultEdge: {
      type: 'line',
      style: {
        stroke: '#A3B1BF',
        lineWidth: 1,
        endArrow: {
          path: G6.Arrow.triangle(8, 8, 0),
          fill: '#A3B1BF'
        }
      }
    }
  })

  graph.data({ nodes, edges })
  graph.render()

  const highlightRelatedNodes = (currentNode) => {
    graph.setAutoPaint(false)
    const relatedNodes = new Set()
    const relatedEdges = new Set()
    relatedNodes.add(currentNode)
    currentNode.getEdges().forEach((edge) => {
      relatedEdges.add(edge)
      const source = edge.getSource()
      const target = edge.getTarget()
      if (source !== currentNode) relatedNodes.add(source)
      if (target !== currentNode) relatedNodes.add(target)
    })
    graph.getNodes().forEach((node) => {
      if (relatedNodes.has(node)) {
        graph.setItemState(node, 'hover', true)
      } else {
        graph.setItemState(node, 'dimmed', true)
      }
    })
    graph.getEdges().forEach((edge) => {
      if (relatedEdges.has(edge)) {
        edge.show()
      } else {
        edge.hide()
      }
    })
    graph.paint()
    graph.setAutoPaint(true)
  }

  graph.on('node:mouseenter', (e) => {
    const node = e.item
    if (!node) return

    highlightRelatedNodes(node)

    // 所有边只显示与当前节点相关的
    const nodeId = node.getID()
    graph.getEdges().forEach((edge) => {
      const model = edge.getModel()
      const isRelated = model.source === nodeId
      edge.changeVisibility(isRelated)
    })
  })

  graph.on('node:mouseleave', (e) => {
    const node = e.item
    if (!node) return

    graph.getNodes().forEach((n) => {
      graph.clearItemStates(n, ['hover', 'dimmed'])
    })

    graph.getEdges().forEach((edge) => edge.show())
  })
}

async function createKnowledgeGraph() {
  if (!props.classId) {
    console.warn('classId 不存在，跳过图谱加载')
    return
  }

  const confirmed = await ElMessageBox.confirm(
    '请确认资源文件存在，否则无法生成图谱，是否继续？',
    '确认生成图谱',
    {
      confirmButtonText: '继续',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).catch(() => false)

  ElMessage.info('知识图谱生成时间较长，请耐心等待...')

  try {
    loading.value = true

    const resourcesRes = await getResourceByClassIdApi(props.classId)
    const resources = resourcesRes.data
    if (!resourcesRes || !resources.length) {
      ElMessage.warning('没有可用的资源')
      return
    }

    const limitedResources = resources.slice(0, 8)

    const urls = limitedResources.map((res: Resource) => PREFIX + res.path.replace(/^\/+/, ''))
    console.log('urls', urls)
    const requestBody = createDifyGraphRequestMulti(urls)

    const graphRes = await createGraphApi(requestBody)

    const graphJson = await fetch(graphRes.data)
      .then((res) => res.json())
      .catch((err) => {
        console.error('下载或解析 JSON 失败', err)
        return null
      })

    if (graphJson) {
      console.log('解析后的图谱数据：', graphJson)
      treeData.value = graphJson
    }

    const normalizeNode = (node: any): any => {
      node.label = node.name || node.label || ''
      node.children = Array.isArray(node.children) ? node.children.map(normalizeNode) : []
      return node
    }

    treeData.value = [normalizeNode(graphJson)]

    const classRes = await getClassesByIdApi(props.classId)
    const relativePath = graphRes.data.replace(PREFIX, '')

    classRes.data.graph = relativePath

    const updateClass: ClassesUpdateDTO = {
      id: classRes.data.id,
      name: classRes.data.name,
      teacherId: classRes.data.teacher.id,
      credit: classRes.data.credit,
      classHours: classRes.data.classHours,
      graph: classRes.data.graph,
      isActive: classRes.data.isActive,
      description: classRes.data.description,
      imageUrl: classRes.data.imageUrl
    }

    await updateClassApi(updateClass)
    await initGraph()
    ElMessage.success('知识图谱创建成功')
  } catch (err) {
    console.error('图谱生成失败', err)
    ElMessage.error('图谱生成失败')
  } finally {
    loading.value = false
  }
}

const deleteKnowledgeGraph = async () => {
  if (!props.classId) {
    console.warn('classId 不存在，跳过图谱删除')
    return
  }

  try {
    const classRes = await getClassesByIdApi(props.classId)

    const updateClass: ClassesUpdateDTO = {
      id: classRes.data.id,
      name: classRes.data.name,
      teacherId: classRes.data.teacher.id,
      credit: classRes.data.credit,
      classHours: classRes.data.classHours,
      graph: '',
      isActive: classRes.data.isActive,
      description: classRes.data.description,
      imageUrl: classRes.data.imageUrl
    }

    treeData.value = []

    await updateClassApi(updateClass)
    ElMessage.success('知识图谱删除成功')
    initGraph()
  } catch (err) {
    console.error('图谱删除失败', err)
    ElMessage.error('图谱删除失败')
  } finally {
    loading.value = false
  }
}

const fetchGraphData = async () => {
  const res = await getClassesByIdApi(props.classId!)
  if (!res.data.graph) {
    ElMessage.warning('该课程暂无图谱数据,请创建图谱')
    return
  }

  const fullUrl = PREFIX + res.data.graph.replace(/^\/+/, '')
  console.log('fullUrl', fullUrl)
  const graphJson = await fetch(fullUrl)
    .then((r) => r.json())
    .catch((err) => {
      console.error('下载或解析 JSON 失败', err)
      return null
    })
  if (!graphJson) return

  if (graphJson) {
    console.log('解析后的图谱数据：', graphJson)
    treeData.value = graphJson
  }

  const normalizeNode = (node: any): any => {
    node.label = node.name || node.label || ''
    node.children = Array.isArray(node.children) ? node.children.map(normalizeNode) : []
    return node
  }

  treeData.value = [normalizeNode(graphJson)]

  initGraph()
}

const fetchFiles = async () => {
  const res = await getResourceByClassIdApi(props.classId!)
  console.log(res)
  fileCards.value = res.data.map((resource: Resource) => ({
    name: resource.name,
    type: resource.type,
    url: resource.path
  }))
}

onMounted(() => {
  if (timer) clearInterval(timer)
  fetchFiles()
  fetchGraphData()
})
</script>

<style scoped>
.knowledge-graph {
  padding: 20px;
}
</style>
