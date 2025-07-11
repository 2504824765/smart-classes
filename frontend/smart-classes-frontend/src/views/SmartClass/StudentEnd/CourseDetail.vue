<script setup lang="ts">
import { onMounted, ref } from 'vue'
import G6 from '@antv/g6'
import { useRouter, useRoute } from 'vue-router'
import FileDisplay from './components/FileDisplay.vue'
import ChatGPT from './components/ChatGPT.vue'
import { getClassesByIdApi } from '@/api/classes'
import { PREFIX } from '@/constants/index'
import { getResourceByClassIdApi } from '@/api/resource/index'
import type { Resource } from '@/api/resource/types'
import { ElMessage } from 'element-plus'

const { push } = useRouter()
const route = useRoute()
const classId = Number(route.query.course)

const courseTitle = ref('课程学习')
const progress = ref(65)
const activeTab = ref('graph')
// 树结构数据
const treeData = ref<any[]>([])

const defaultProps = {
  children: 'children',
  label: 'label'
}

// 文件资源列表（后续替换为 FileCard 组件）
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

// 图容器 DOM 引用
const graphContainer = ref<HTMLDivElement>()

// 点击树结构或图谱节点
const handleTreeClick = (node: any) => {}

const registerCustomNode = () => {
  G6.registerNode(
    'progress-node',
    {
      draw(cfg, group) {
        const size = (Array.isArray(cfg.size) ? cfg.size[0] : cfg.size) || 50
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

// 初始化 G6 图谱
const initGraph = async () => {
  if (!graphContainer.value) {
    console.error('图谱容器未找到')
    return
  }

  // 获取容器尺寸，如果为0则设置默认值
  const container = graphContainer.value
  const containerWidth = container.clientWidth || 800
  const containerHeight = container.clientHeight || 600

  console.log('容器尺寸:', containerWidth, containerHeight)

  if (containerWidth === 0 || containerHeight === 0) {
    console.warn('容器尺寸为0，延迟初始化')
    setTimeout(() => initGraph(), 200)
    return
  }

  const nodes: any[] = []
  const edges: any[] = []

  const walkTreeList = (treeList: any[], parentId: string | null = null) => {
    for (const node of treeList) {
      nodes.push({
        id: node.id,
        label: node.label,
        type: 'progress-node',
        progress: node.progress ?? Math.random(), // 示例随机进度，可自定义
        style: {
          fill: parentId ? '#4BABF4' : '#5B8FF9',
          stroke: '#5B8FF9'
        }
      })

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
  console.log(nodes, edges)
  const graph = new G6.Graph({
    container: graphContainer.value!,
    width: graphContainer.value!.offsetWidth,
    height: graphContainer.value!.offsetHeight,
    layout: {
      type: 'force',
      center: [containerWidth / 2, containerHeight / 2], // 设置中心点
      linkDistance: 100, // 连线长度
      maxIteration: 1000, // 最大迭代次数
      focusNode: '1', // 以根节点为中心
      unitRadius: 120, // 每一层的半径间距
      preventOverlap: true, // 防止节点重叠
      nodeSize: 90, // 节点大小（用于防重叠计算）
      strictRadial: false // 不严格按径向排列，允许微调
    },
    modes: {
      default: ['drag-canvas', 'zoom-canvas', 'drag-node']
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

  graph.on('node:click', (e) => {
    const node = e.item
    if (!node) return

    const model = node.getModel()
    const knowledgeName = model.label
    push({
      path: '/course/knowledge',
      query: { classId: classId, knowledgeName: knowledgeName as string }
    })
  })
}

const fetchGraphData = async () => {
  const res = await getClassesByIdApi(classId!)
  if (!res.data) {
    ElMessage.warning('无法定位课程')
    return
  }
  courseTitle.value = res.data.name
  const fullUrl = PREFIX + res.data.graph.replace(/^\/+/, '')

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
}

const fetchFiles = async () => {
  const res = await getResourceByClassIdApi(classId!)
  fileCards.value = res.data.map((resource: Resource) => ({
    name: resource.name,
    type: resource.type,
    url: resource.path
  }))
}

// 页面挂载后初始化图谱
onMounted(async () => {
  registerCustomNode()
  await fetchFiles()
  await fetchGraphData()
  await initGraph()
})
</script>

<template>
  <div class="p-4">
    <!-- 顶部信息 -->
    <el-card class="mb-4">
      <div class="flex justify-between items-center">
        <div>
          <h2 class="text-xl font-bold">课程名称：{{ courseTitle }}</h2>
        </div>
      </div>
    </el-card>

    <!-- 主体区域：三栏布局 -->
    <div class="grid grid-cols-12 gap-4 h-[80vh]">
      <!-- 左：树结构 -->
      <el-card class="col-span-2 overflow-auto">
        <el-tree :data="treeData" :props="defaultProps" @node-click="handleTreeClick">
          <template #default="{ node, data }">
            <div class="node-text">
              {{ node.label }}
            </div>
          </template>
        </el-tree>
      </el-card>

      <!-- 中：图谱/资源 tabs -->
      <el-card class="col-span-7 flex flex-col">
        <el-tabs v-model="activeTab" class="flex-1" tab-position="top">
          <!-- 图谱 tab -->
          <el-tab-pane label="图谱" name="graph">
            <div
              v-if="treeData.length > 0"
              ref="graphContainer"
              class="w-full h-[calc(80vh-120px)]"
              style="min-height: 500px"
            ></div>
            <el-empty v-else description="暂无图谱数据" />
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

      <!-- 右：ChatGPT -->
      <div class="col-span-3 h-[80vh]">
        <ChatGPT />
      </div>
    </div>
  </div>
</template>

<style scoped>
.node-text {
  max-width: 160px; /* 根据需要设置最大宽度 */
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
