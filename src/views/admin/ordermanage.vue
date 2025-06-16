<template>
  <div>
    <div style="margin-bottom: 10px; display: flex; gap: 10px;">
      <el-input v-model="keyword" placeholder="请输入商品名称进行搜索" clearable style="width: 240px;" />
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>

    <el-table :data="orderList" border style="margin-top: 10px;" stripe>
      <el-table-column prop="id" label="订单ID" width="80" />
      <el-table-column prop="userId" label="用户ID" width="100" />
      <el-table-column prop="status" label="状态" />
      <el-table-column prop="totalPrice" label="总价" />
      <el-table-column prop="createTime" label="下单时间" />
      <el-table-column label="订单项">
        <template #default="scope">
          <div v-for="item in scope.row.items" :key="item.id">
            {{ item.name }} × {{ item.quantity }}
          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { getAllOrders, searchOrdersByKeyword } from '@/services/order';
import useUserStore from '@/store/modules/userStore.js';

const userStore = useUserStore();
const token = userStore.token;

const orderList = ref([]);
const keyword = ref('');

// 获取全部订单（管理员）
const fetchAllOrders = async () => {
  try {
    const res = await getAllOrders(token);
    if (res.code === 200) {
      orderList.value = res.data;
    } else {
      ElMessage.error(res.msg || '获取订单失败');
    }
  } catch (e) {
    ElMessage.error('网络错误');
  }
};

// 模糊搜索
const handleSearch = async () => {
  if (!keyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词');
    return;
  }
  try {
    const res = await searchOrdersByKeyword(keyword.value);
    if (res.code === 200) {
      orderList.value = res.data;
    } else {
      ElMessage.error(res.msg || '搜索失败');
    }
  } catch (e) {
    ElMessage.error('网络错误');
  }
};

// 重置搜索
const resetSearch = () => {
  keyword.value = '';
  fetchAllOrders();
};

onMounted(() => {
  fetchAllOrders();
});
</script>
