<template>
  <div class="user-list">
    <el-row :gutter="20">
      <el-col
          v-for="user in users"
          :key="user.id"
          :xs="24" :sm="12" :md="8" :lg="6"
      >
        <el-card :body-style="{ padding: '15px' }" shadow="hover">
          <div class="user-info">
            <h3>{{ user.username }}</h3>
            <p class="email">邮箱: {{ user.email || '无' }}</p>
            <p class="role">角色: {{ user.role }}</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <div class="btn-area">
      <el-button type="primary" @click="loadUsers" :loading="loading">
        刷新
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { fetchAllUsers } from '@/services/user.js'
import useUserStore from '@/store/modules/userStore.js'

const users = ref([])
const loading = ref(false)

async function loadUsers() {
  loading.value = true
  try {
    const userStore = useUserStore()
    const token = userStore.token
    if (!token) {
      ElMessage.error('请先登录')
      loading.value = false
      return
    }

    const res = await fetchAllUsers(token)
    if (res.data.code === 200) {
      users.value = res.data.data
    } else {
      ElMessage.error(res.data.msg || '获取用户失败')
    }
  } catch (error) {
    ElMessage.error('请求失败，请检查网络')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.user-list {
  padding: 20px;
}

.user-info {
  padding: 10px 0;
}

h3 {
  font-weight: 600;
  margin-bottom: 5px;
  font-size: 18px;
  color: #409EFF;
}

.email, .role {
  font-size: 14px;
  color: #666;
  margin: 2px 0;
}

.btn-area {
  margin-top: 20px;
  text-align: center;
}
</style>
