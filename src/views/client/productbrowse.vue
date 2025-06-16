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
            <router-link :to="`/client/detail/${item.id}`">
              <el-button type="primary" size="small">查看详情</el-button>
            </router-link>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getProducts } from '@/services/product.js'

const productList = ref([])
const defaultImage = 'https://via.placeholder.com/200x150?text=No+Image'

onMounted(async () => {
  try {
    const response = await getProducts()
    console.log("API原始响应:", response)

    // 提取实际数据部分 - 根据API响应结构调整
    let rawData = []

    // 情况1：API返回 { data: { code:200, data: [...] } }
    if (response?.data?.code === 200 && Array.isArray(response.data.data)) {
      rawData = response.data.data
    }
    // 情况2：API返回 { data: [...] }
    else if (Array.isArray(response?.data)) {
      rawData = response.data
    }
    // 情况3：API直接返回数组
    else if (Array.isArray(response)) {
      rawData = response
    }
    // 其他格式处理
    else if (response?.data?.items && Array.isArray(response.data.items)) {
      rawData = response.data.items
    }
    else {
      console.error('无法解析的产品数据格式', response)
      productList.value = []
      return
    }

    // 处理数据并赋值
    productList.value = rawData.map(item => {
      // 确保字段匹配数据库结构
      return {
        id: item.id || 0,
        name: item.name || '未命名商品',
        description: item.description || '',
        price: typeof item.price === 'number' ? item.price : Number(item.price) || 0,
        image_url: item.image_url || item.image || defaultImage,
        // 添加其他必要字段...
      }
    })

    console.log("处理后的产品数据:", productList.value)

  } catch (error) {
    console.error('获取产品列表失败:', error)
    productList.value = []
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
