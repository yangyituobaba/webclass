<template>
  <el-card class="product-card" v-if="product">
    <template #header>
      <h2>{{ product.name }}</h2>
    </template>

    <div class="content">
      <img :src="product.image" alt="商品图片" class="product-image" />

      <div class="info">
        <p class="price">价格：￥{{ product.price }}</p>
        <p class="description">{{ product.description }}</p>

        <el-button type="primary" @click="addToCart" :disabled="!product">加入购物车</el-button>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import useCartStore from '@/store/modules/cartStore';
import { getProductDetail } from '@/services/product.js';

const route = useRoute();
const cartStore = useCartStore();
const product = ref(null);

const addToCart = () => {
  if (!product.value) {
    ElMessage.error("商品信息未加载，无法加入购物车");
    return;
  }
  cartStore.addItem({
    productId: product.value.id,
    name: product.value.name,
    price: product.value.price,
    quantity: 1
  });
  ElMessage.success("已加入购物车");
};

onMounted(async () => {
  const id = route.params.id;
  const res = await getProductDetail(id);
  product.value = res.data.data;
});
</script>

<style scoped>
.product-card {
  max-width: 800px;
  margin: 40px auto;
  padding: 24px;
}
.content {
  display: flex;
  gap: 24px;
}
.product-image {
  width: 300px;
  height: auto;
  border-radius: 8px;
  object-fit: contain;
  border: 1px solid #eee;
}
.info {
  flex: 1;
}
.price {
  color: #e91e63;
  font-size: 20px;
  margin: 16px 0;
}
.description {
  color: #666;
  margin-bottom: 20px;
}
</style>
