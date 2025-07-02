<template>
  <div class="grid grid-cols-12 gap-3 h-[100vh]">
    <el-card class="col-span-3 overflow-auto">
      <el-tree :data="treeData" :props="defaultProps" />
    </el-card>
    <el-card class="col-span-9 flex flex-col">
      <el-tabs v-model="activeTab" class="flex-1" tab-position="top">
        <!-- 图谱 tab -->
        <el-tab-pane label="图谱" name="graph">
          <div class="flex items-center justify-between mb-2">
            <div class="flex items-center">
              <ElButton type="primary" @click="createKnowledgeGraph">生成图谱</ElButton>
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
import { ref, onMounted } from 'vue'
import G6 from '@antv/g6'
import FileDisplay from './FileDisplay.vue'
import { getResourceByClassIdApi } from '@/api/resource/index'
import type { Resource } from '@/api/resource/types'
import { getClassesByIdApi } from '@/api/classes'
import { PREFIX } from '@/constants/index'
import { createGraphApi } from '@/api/dify/index'
import { createDifyGraphRequestMulti } from '@/api/dify/types'

const props = defineProps<{ classId: number | undefined }>()

const graphContainer = ref<HTMLDivElement>()

const treeData = ref([])
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

const initGraph = async () => {
  if (!props.classId) {
    console.warn('classId 不存在，跳过图谱加载')
    return
  }

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
      type: 'radial',
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
}

async function createKnowledgeGraph() {
  if (!props.classId) {
    console.warn('classId 不存在，跳过图谱加载')
    return
  }
  const resourcesRes = await getResourceByClassIdApi(props.classId)
  console.log('resourcesRes', resourcesRes)
  const resources = resourcesRes.data
  if (!resourcesRes) return
  const urls = resources.map((res: Resource) => res.path)
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
}

const fetchGraphData = async () => {
  const res = await getClassesByIdApi(props.classId!)

  // 拼接完整图谱路径
  const fullUrl = PREFIX + res.data.graph

  const graphJson = await fetch(fullUrl)
    .then((res) => res.json())
    .catch((err) => {
      console.error('下载或解析 JSON 失败', err)
      return null
    })

  if (graphJson) {
    console.log('解析后的图谱数据：', graphJson)
    treeData.value = graphJson
  }
}

const fetchFiles = async () => {
  const res = await getResourceByClassIdApi(props.classId!)
  fileCards.value = res.data.map((resource: Resource) => ({
    name: resource.name,
    type: resource.type,
    url: resource.path
  }))
}

onMounted(() => {
  fetchFiles()
  fetchGraphData()
  initGraph()
})
</script>

<style scoped>
.knowledge-graph {
  padding: 20px;
}
</style>
