<template>
  <el-card>
    <h2>我的订单</h2>
    <el-table
        :data="orders"
        border
        style="width: 100%"
        row-key="id"
    >
      <!-- 展开行：子表格展示订单商品项 -->
      <el-table-column>
        <template #default="{ row }">
          <el-table
              :data="row.items"
              border
              size="small"
              style="width: 100%"
          >
            <el-table-column prop="productName" label="商品名称" min-width="150" />
            <el-table-column prop="quantity" label="数量" width="80" />
            <el-table-column prop="price" label="单价" width="100" />
            <el-table-column
                label="小计"
                width="100"
                :formatter="(row) => '¥' + (row.quantity * row.price).toFixed(2)"
            />
          </el-table>
        </template>
      </el-table-column>

      <!-- 主表列 -->
      <el-table-column prop="id" label="订单号" width="100" />
      <el-table-column label="用户名称" width="120" :formatter="formatUsername" />
      <el-table-column prop="status" label="订单状态" width="120" />
      <el-table-column
          prop="total_price"
          label="总价"
          width="100"
          :formatter="formatPrice"
      />
      <el-table-column prop="create_time" label="下单时间" width="180" />
    </el-table>

    <el-empty v-if="orders.length === 0" description="暂无订单" />
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getOrders } from '@/services/order.js'
import { getUserProfile } from '@/services/user.js'
import useUserStore from "@/store/modules/userStore.js";

const orders = ref([])
const usersMap = ref({})

function formatPrice(row, column, cellValue) {
  if (typeof cellValue === 'number') {
    return '¥' + cellValue.toFixed(2);
  }
  return '¥0.00';
}

function formatUsername(row) {
  return usersMap.value[row.userId] || '未知用户';
}


async function fetchOrders() {
  try {
    const userStore = useUserStore()
    const username = userStore.username

    // 传用户名查询当前用户订单
    const res = await getOrders(username)

    orders.value = res.data.data.map(order => ({
      ...order,
      items: order.items ? order.items.map(item => ({
        productName: item.name || item.product_name,
        quantity: item.quantity,
        price: item.price
      })) : []
    }))
  } catch (err) {
    console.error('获取订单失败:', err)
  }
}


onMounted(async () => {
  await fetchOrders()
})
</script>

<style scoped>
h2 {
  margin-bottom: 20px;
}
</style>
