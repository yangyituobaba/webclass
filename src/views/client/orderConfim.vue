<template>
  <div class="order-confirm">
    <h2>确认订单</h2>

    <div v-if="items.length === 0">购物车为空</div>

    <div v-else>
      <ul>
        <li v-for="item in items" :key="item.productId">
          {{ item.name }} × {{ item.quantity }} = ￥{{ (item.price * item.quantity).toFixed(2) }}
        </li>
      </ul>
      <p>总价：<strong>￥{{ totalPrice.toFixed(2) }}</strong></p>

      <el-button type="primary" @click="submitOrder">提交订单</el-button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import axios from 'axios';

import { createOrder } from '@/services/order.js';
import useCartStore from '@/store/modules/cartStore';
import useUserStore from '@/store/modules/userStore';

const router = useRouter();
const cartStore = useCartStore();
const userStore = useUserStore();

const items = computed(() => cartStore.items);
const totalPrice = computed(() => cartStore.totalPrice);
const username = computed(() => userStore.username);

const submitOrder = async () => {
  if (!username.value) {
    ElMessage.error("请先登录");
    return;
  }

  if (items.value.length === 0) {
    ElMessage.warning("购物车为空");
    return;
  }

  try {
    const order = {
      username: username.value,
      order: {
        items: items.value.map(item => ({
          productId: item.productId,
          name: item.name,
          quantity: item.quantity,
          price: item.price,
        })),
        totalPrice: totalPrice.value,
        address: "北京市朝阳区XXX街道",  // 这里示范写死，实际要动态输入
        status: "pending"
      }
    };

    const res = await createOrder(order);

    if (res.data.code === 200) {
      ElMessage.success("订单创建成功！");
      cartStore.clearCart();
    } else {
      ElMessage.error(res.data.msg || "创建失败");
    }
  } catch (error) {
    console.error(error);
    ElMessage.error("网络错误");
  }
};

</script>

<style scoped>
.order-confirm {
  max-width: 600px;
  margin: 20px auto;
  padding: 20px;
  border: 1px solid #eee;
  border-radius: 8px;
  background-color: #fafafa;
}
</style>
