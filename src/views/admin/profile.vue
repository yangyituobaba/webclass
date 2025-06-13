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
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserProfile } from '@/services/user'
import useUserStore from '@/store/modules/userStore.js'
import {getOrders} from "@/services/order.js";

const userStore = useUserStore()
const profile = ref({})
const loading = ref(true)

const roleMap = {
  client: '客户',
  admin: '管理员',
  delivery: '配送员'
}


//我的订单

const orders = ref([])


function formatPrice(row, column, cellValue) {
  if (typeof cellValue === 'number') {
    return '¥' + cellValue.toFixed(2);
  }
  return '¥0.00';
}

async function fetchOrders() {
  try {
    const userStore = useUserStore()
    const token = userStore.token

    // 调用后端接口获取订单列表
    const res = await getOrders(token)

    // 处理返回数据，映射items中的商品名称，补充user信息
    orders.value = res.data.map(order => ({
      ...order,
      items: order.items ? order.items.map(item => ({
        productName: item.product?.name || item.productName || item.name || item.product_name || '无商品名',
        quantity: item.quantity,
        price: item.price
      })) : [],
      // 这里假设order中user对象包含用户名和地址
      user: order.user || {},
      remark: order.remark || ''
    }))
  } catch (err) {
    console.error('获取订单失败:', err)
  }
}

const editDialogVisible = ref(false)
const editingOrder = ref(null)
const editForm = ref({
  address: '',
  remark: ''
})

const handleEdit = (order) => {
  editingOrder.value = { ...order }
  if (order.status === '待配送') {
    editForm.value.address = order.user?.address || ''
    editForm.value.remark = order.remark || ''
  }
  editDialogVisible.value = true
}

const submitEdit = () => {
  if (!editingOrder.value) return
  const status = editingOrder.value.status

  if (status === '待配送') {
    // 模拟更新地址和备注逻辑
    editingOrder.value.user.address = editForm.value.address
    editingOrder.value.remark = editForm.value.remark
  } else if (status === '已送达') {
    // 修改为已收货
    editingOrder.value.status = '已收货'
  }

  editDialogVisible.value = false
}

const cancelOrder = () => {
  if (editingOrder.value?.status === '待配送') {
    editingOrder.value.status = '已取消'
    editDialogVisible.value = false
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
  await fetchOrders()
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
