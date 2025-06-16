<template>
  <div class="order-confirm">
    <h2>确认订单</h2>

    <div v-if="items.length === 0" class="empty-cart">
      <el-empty description="购物车为空" />
    </div>

    <div v-else>
      <el-card shadow="hover" class="order-card">
        <el-checkbox-group v-model="selectedItemIds">
          <ul class="order-items">
            <li v-for="item in items" :key="item.productId" class="order-item">
              <el-checkbox :label="item.productId" class="item-checkbox" />
              <span class="item-name">{{ item.name }}</span>
              <span class="item-quantity">× {{ item.quantity }}</span>
              <span class="item-total">￥{{ (item.price * item.quantity).toFixed(2) }}</span>
            </li>
          </ul>
        </el-checkbox-group>

        <div class="order-summary">
          <span>总价：</span>
          <strong class="total-price">￥{{ selectedTotalPrice.toFixed(2) }}</strong>
        </div>

        <el-form :model="form" ref="orderForm" label-width="80px" class="order-form">
          <el-form-item label="收货地址" prop="address" :rules="[{ required: true, message: '请输入收货地址', trigger: 'blur' }]">
            <el-input v-model="form.address" placeholder="请输入收货地址" />
          </el-form-item>

          <el-form-item label="联系电话" prop="phone" :rules="[
            { required: true, message: '请输入联系电话', trigger: 'blur' },
            { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号', trigger: 'blur' }
          ]">
            <el-input v-model="form.phone" placeholder="请输入联系电话" />
          </el-form-item>

          <el-form-item label="备注">
            <el-input
                type="textarea"
                v-model="form.remark"
                placeholder="订单备注（选填）"
                :rows="3"
                show-word-limit
                maxlength="200"
            />
          </el-form-item>
        </el-form>

        <el-button
            type="primary"
            class="submit-btn"
            @click="submitOrder"
            :loading="loading"
            :disabled="loading"
        >
          提交订单
        </el-button>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';

import {cacheOrderConfirm, createOrder} from '@/services/order.js';
import useCartStore from '@/store/modules/cartStore';
import useUserStore from '@/store/modules/userStore';

const router = useRouter();
const cartStore = useCartStore();
const userStore = useUserStore();

const items = computed(() => cartStore.items);

const selectedItemIds = ref(items.value.map(item => item.productId)); // 默认全选
const selectedItems = computed(() =>
    items.value.filter(item => selectedItemIds.value.includes(item.productId))
);

const selectedTotalPrice = computed(() =>
    selectedItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
);

const loading = ref(false);
const orderForm = ref(null);

const form = ref({
  address: '',
  phone: '',
  remark: ''
});


const submitOrder = () => {
  if (!userStore.token) {
    ElMessage.error("请先登录");
    return;
  }

  if (selectedItems.value.length === 0) {
    ElMessage.warning("请选择至少一个订单项");
    return;
  }

  orderForm.value.validate(async (valid) => {
    if (!valid) return;

    loading.value = true;
    try {
      const order = {
        items: selectedItems.value.map(item => ({
          productId: item.productId,
          quantity: item.quantity,
          price: item.price
        })),
        totalPrice: selectedTotalPrice.value,
        address: form.value.address,
        contactPhone: form.value.phone,
        remark: form.value.remark,
        status: "pending"
      };

      // 先缓存订单确认信息
      const cacheRes = await cacheOrderConfirm(order, userStore.token);
      if (cacheRes.data.code !== 200) {
        ElMessage.error(cacheRes.data.msg || "缓存订单失败");
        loading.value = false;
        return;
      }

      // 再调用创建订单接口，body 为空
      const createRes = await createOrder(userStore.token);
      if (createRes.data.code === 200) {
        ElMessage.success("订单创建成功！");
        cartStore.clearCart();
        router.push("/client/profile");
      } else {
        ElMessage.error(createRes.data.msg || "创建失败");
      }
    } catch (error) {
      console.error(error);
      ElMessage.error("网络错误");
    } finally {
      loading.value = false;
    }
  });
};
</script>

<style scoped>
.order-confirm {
  margin: 30px auto;
  padding: 24px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 20px rgb(0 0 0 / 0.1);
}

h2 {
  font-weight: 600;
  font-size: 1.8rem;
  margin-bottom: 20px;
  color: #2c3e50;
  text-align: center;
}

.empty-cart {
  margin-top: 40px;
  text-align: center;
  color: #909399;
}

.order-card {
  padding: 20px;
}

.order-items {
  list-style: none;
  padding: 0;
  margin: 0 0 20px;
  border-bottom: 1px solid #ebeef5;
  max-height: 300px;
  overflow-y: auto;
}

.order-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 0;
  font-size: 1rem;
  color: #606266;
}

.item-checkbox {
  margin-right: 10px;
}

.item-name {
  flex: 1 1 auto;
}

.item-quantity {
  width: 80px;
  text-align: center;
  color: #909399;
}

.item-total {
  width: 100px;
  text-align: right;
  font-weight: 600;
  color: #409eff;
}

.order-summary {
  display: flex;
  justify-content: flex-end;
  font-size: 1.2rem;
  margin-bottom: 20px;
  color: #303133;
}

.total-price {
  color: #f56c6c;
  margin-left: 8px;
}

.order-form {
  margin-bottom: 20px;
}

.submit-btn {
  display: block;
  width: 100%;
}
</style>
