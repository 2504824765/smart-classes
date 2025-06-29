<script setup lang="ts">
import { onMounted, ref, reactive } from 'vue'
import G6 from '@antv/g6'
import { ElMessage } from 'element-plus'

// 页面数据
const courseTitle = ref('Python 深度学习')
const progress = ref(65)

// 树结构数据
const treeData = ref([
  {
    label: 'Python',
    children: [
      {
        label: '深度学习',
        children: [
          {
            label: '神经网络',
            children: [
              { label: '卷积神经网络', children: [{ label: '过滤器' }, { label: '池化算法' }] },
              { label: '密集连接网络' },
              { label: '分层表示学习' },
            ],
          },
          { label: '反向传播算法' },
          { label: '正则化' },
        ],
      },
      {
        label: '机器学习',
        children: [{ label: '核方法' }, { label: '梯度提升机' }],
      },
      { label: '向量化实现' },
      { label: '距离函数' },
    ],
  },
])
const defaultProps = {
  children: 'children',
  label: 'label',
}

// 文件资源列表（后续替换为 FileCard 组件）
const fileCards = ref([
  { name: '神经网络.pdf', type: 'PDF' },
  { name: '深度学习.pptx', type: 'PPTX' },
  { name: 'Python源码.zip', type: 'ZIP' },
])

// 图容器 DOM 引用
const graphContainer = ref<HTMLDivElement>()

// 点击树结构或图谱节点
const handleTreeClick = (node: any) => {
  ElMessage.success(`点击课程节点：${node.label}`)
}

// 初始化 G6 图谱
const initGraph = async () => {
    const res = await fetch('/api/course-graph') // 替换为后端接口
    const json = await res.json()

    const nodes: any[] = []
    const edges: any[] = []

    const walk = (node: any, parentId: string | null = null) => {
        nodes.push({ id: node.id, label: node.id, style: node.style || {} })
        if (parentId) edges.push({ source: parentId, target: node.id })
        if (node.children) node.children.forEach((c: any) => walk(c, node.id))
    }
    walk(json)

const graph = new G6.Graph({
  container: graphContainer.value!,
  width: graphContainer.value!.offsetWidth,
  height: graphContainer.value!.offsetHeight,
  layout: {
    type: 'compactBox',
    direction: 'LR',
    getId: (d) => d.id,
    getHeight: () => 60,
    getWidth: () => 120,
    getVGap: () => 20,
    getHGap: () => 100,
  },
  node: {
    type: 'rect', // rect 是内置节点类型
    style: {
      fill: '#fff',
      stroke: '#5B8FF9',
      radius: 4,
    },
    label: {
      // ✅ 正确写法：label 为对象
      fields: ['id'], // 默认取字段
      style: {
        fontSize: 14,
        fill: '#000',
      },
    },
  },
  edge: {
    type: 'polyline',
    style: {
      stroke: '#A3B1BF',
    },
  },
  modes: {
    default: ['drag-canvas', 'zoom-canvas'],
  },
})


    graph.data({ nodes, edges })
    graph.render()
}

// 页面挂载后初始化图谱
onMounted(() => {
  initGraph()
})
</script>

<template>
  <div class="p-4">
    <!-- 顶部信息 -->
    <el-card class="mb-4">
      <div class="flex justify-between items-center">
        <div>
          <h2 class="text-xl font-bold">课程名称：{{ courseTitle }}</h2>
          <p class="text-gray-500">已学习进度：{{ progress }}%</p>
        </div>
      </div>
    </el-card>

    <div class="grid grid-cols-12 gap-4">
      <!-- 树结构 -->
      <el-card class="col-span-2 h-[80vh] overflow-auto">
        <el-tree
          :data="treeData"
          :props="defaultProps"
          @node-click="handleTreeClick"
        />
      </el-card>

      <!-- 图谱 -->
      <el-card class="col-span-7 h-[80vh]">
        <div ref="graphContainer" class="w-full h-full" />
      </el-card>

      <!-- 文件卡片列表 -->
      <el-card class="col-span-3 h-[80vh] overflow-auto">
        <div class="text-lg font-semibold mb-2">资源文件</div>
        <el-empty v-if="fileCards.length === 0" description="暂无资源" />
        <el-card
          v-for="file in fileCards"
          :key="file.name"
          class="mb-2"
        >
          <p>文件名：{{ file.name }}</p>
          <p>类型：{{ file.type }}</p>
        </el-card>
      </el-card>
    </div>
  </div>
</template>
