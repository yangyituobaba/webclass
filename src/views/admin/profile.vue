<template>
  <div class="profile-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>我的主页</h2>
        </div>
      </template>

      <el-skeleton :loading="loading" animated>
        <template #default>
          <el-descriptions title="基本信息" :column="1" border>
            <el-descriptions-item label="姓名">{{ profile.name || '未填写' }}</el-descriptions-item>
            <el-descriptions-item label="用户名">{{ profile.username || '未填写' }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ profile.phone || '未填写' }}</el-descriptions-item>
            <el-descriptions-item label="角色">{{ roleMap[profile.role] || profile.role || '未知' }}</el-descriptions-item>
          </el-descriptions>
        </template>
      </el-skeleton>
    </el-card>
  </div>
  <!-- 管理员统计区域 -->
  <el-card class="admin-statistics" style="margin-top: 30px;">
    <template #header>
      <div class="card-header">
        <h2>管理员统计信息</h2>
      </div>
    </template>

    <div class="statistics-container" style="display: flex; gap: 20px;">
      <!-- 左侧饼图 -->
      <div ref="pieChart" style="width: 400px; height: 300px;"></div>

      <!-- 右侧详细数字 -->
      <div class="stats-details" style="flex: 1; font-size: 16px; line-height: 2;">
        <p>订单总数：{{ stats.totalOrders }}</p>
        <p>待配送订单数（Pending）：{{ stats.statusCounts.pending || 0 }}</p>
        <p>配送中订单数（Delivering）：{{ stats.statusCounts.delivering || 0 }}</p>
        <p>已完成订单数（Completed）：{{ stats.statusCounts.completed || 0 }}</p>
        <p>总成交金额：¥{{ stats.totalRevenue.toFixed(2) }}</p>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { ref, onMounted,watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserProfile } from '@/services/user'
import useUserStore from '@/store/modules/userStore.js'
import {getAdminStatistics} from "@/services/order.js";
import * as echarts from 'echarts'

const userStore = useUserStore()
const profile = ref({})
const loading = ref(true)

const roleMap = {
  client: '客户',
  admin: '管理员',
  delivery: '配送员'
}

//饼图
const stats = ref({
  totalOrders: 0,
  statusCounts: {
    pending: 0,
    delivering: 0,
    completed: 0
  },
  totalRevenue: 0
})


const pieChart = ref(null)
let chartInstance = null
const orders = ref([])

// 初始化饼图
function initChart() {
  if (!pieChart.value) return
  chartInstance = echarts.init(pieChart.value)

  const option = {
    title: {
      text: '订单状态分布',
      left: 'center',
      top: 10,
      textStyle: { fontSize: 16 }
    },
    tooltip: {
      trigger: 'item'
    },
    legend: {
      bottom: 10,
      left: 'center',
      data: ['Pending', 'Delivering', 'Completed']
    },
    series: [
      {
        name: '订单状态',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        label: {
          show: true,
          position: 'outside',
          formatter: '{b}: {c} ({d}%)'
        },
        labelLine: {
          show: true
        },
        data: [
          { value: stats.value.statusCounts.pending || 0, name: 'Pending' },
          { value: stats.value.statusCounts.delivering || 0, name: 'Delivering' },
          { value: stats.value.statusCounts.completed || 0, name: 'Completed' }
        ]
      }
    ]
  }

  chartInstance.setOption(option)
}

// 更新饼图数据
function updateChart() {
  if (!chartInstance) return
  chartInstance.setOption({
    series: [
      {
        data: [
          { value: stats.value.statusCounts.pending || 0, name: 'Pending' },
          { value: stats.value.statusCounts.delivering || 0, name: 'Delivering' },
          { value: stats.value.statusCounts.completed || 0, name: 'Completed' }
        ]
      }
    ]
  })
}

// 监听数据变化，更新饼图
watch(stats, () => {
  if (!chartInstance) {
    initChart()
  } else {
    updateChart()
  }
}, { deep: true })

// 获取后台统计数据
async function fetchStatistics() {
  try {
    const res = await getAdminStatistics()
    console.log(res)
    if (res.code === 200) {
      stats.value = res.data
    }
  } catch (e) {
    console.error('获取管理员统计信息失败:', e)
  }
}

onMounted(async () => {
  const username = userStore.username
  const token = userStore.token

  if (!username || !token) {
    ElMessage.error('未找到登录信息，请重新登录')
    loading.value = false
    // 跳转到登录页（如有必要）
    return
  }

  try {
    const res = await getUserProfile(token)
    if (res.code === 200) {
      profile.value = res.data
    } else {
      ElMessage.error(res.msg || '获取用户信息失败')
    }
  } catch (err) {
    ElMessage.error('请求用户信息出错')
  } finally {
    loading.value = false
  }
  await fetchStatistics()
})
</script>


<style scoped>
.profile-container {
  margin: 5px auto; /* 水平居中，顶部底部间距50px */
  padding: 5px;
}

.card-header {
  font-weight: bold;
}
.card-header2 {
  font-weight: bold;
}
h2 {
  margin-bottom: 20px;
}
</style>
