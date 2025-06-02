<template>
  <div class="product-list">
    <el-row :gutter="20">
      <el-col
          v-for="item in productList"
          :key="item.id"
          :xs="24" :sm="12" :md="8" :lg="6"
      >
        <el-card :body-style="{ padding: '10px' }" shadow="hover">
          <img
              :src="item.image_url || defaultImage"
              alt="产品图片"
              class="product-image"
          />
          <div class="product-info">
            <h3>{{ item.name }}</h3>
            <p class="desc">{{ item.description }}</p>
            <div class="price">￥{{ item.price.toFixed(2) }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import useUserStore from "@/store/modules/userStore.js";

const userStore = useUserStore()
const productList = ref([])
const defaultImage = 'https://via.placeholder.com/200x150?text=No+Image'

onMounted(async () => {
  const res = await userStore.getProductList()
  if (res && Array.isArray(res)) {
    productList.value = res
  } else {
    console.error('获取产品列表失败')
  }
})
</script>

<style scoped>
.product-list {
  padding: 20px;
}

.product-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
  border-radius: 6px;
}

.product-info {
  margin-top: 10px;
}

.desc {
  font-size: 14px;
  color: #666;
  margin: 5px 0;
  height: 40px;
  overflow: hidden;
  text-overflow: ellipsis;
}

.price {
  color: #e91e63;
  font-weight: bold;
  font-size: 16px;
}
</style>
