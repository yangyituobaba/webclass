<template>
  <div>
    <div style="margin-bottom: 10px; display: flex; gap: 10px;">
      <el-input v-model="searchKeyword" placeholder="请输入产品名称" clearable style="width: 240px;" />
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="resetSearch">重置</el-button>
      <el-button type="success" @click="openDialog()">新增产品</el-button>
    </div>

    <el-table :data="productList" border style="margin-top: 10px;">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="产品名称" />
      <el-table-column prop="price" label="价格" />
      <el-table-column prop="description" label="描述" />
      <el-table-column prop="imageUrl" label="图片">
        <template #default="scope">
          <img :src="scope.row.imageUrl" alt="产品图片" style="width: 50px; height: 50px;" v-if="scope.row.imageUrl"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button type="primary" size="small" @click="openDialog(scope.row)">编辑</el-button>
          <el-button type="danger" size="small" @click="deletethisProduct(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 弹窗：新增 / 编辑 -->
    <el-dialog :title="form.id ? '编辑产品' : '新增产品'" v-model="dialogVisible">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>

        <el-form-item label="价格">
          <el-input v-model="form.price" type="number" />
        </el-form-item>

        <el-form-item label="描述">
          <el-input type="textarea" v-model="form.description" />
        </el-form-item>

        <el-form-item label="库存">
          <el-input-number v-model="form.amount" :min="0" />
        </el-form-item>

        <el-form-item label="图片">
          <el-upload
              class="upload-demo"
              :http-request="customUpload"
              :show-file-list="false"
              accept="image/*"
          >
            <el-button type="primary">点击上传图片</el-button>
          </el-upload>
          <div v-if="form.imageUrl" style="margin-top: 10px;">
            <img :src="form.imageUrl" alt="预览图" style="width: 80px;" />
          </div>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  getProducts,
  addProduct,
  updateProduct,
  deleteProduct,
  searchProductsByName,
  uploadProductImage
} from '@/services/product.js';
import useUserStore from '@/store/modules/userStore.js';

const productList = ref([]);
const dialogVisible = ref(false);
const form = ref({});
const searchKeyword = ref('');

const userStore = useUserStore();
const token = userStore.token;
const userId = userStore.userId;  // 取 userId

// 初始化产品列表
const fetchProducts = async () => {
  const res = await getProducts();
  if (res.data.code === 200) {
    productList.value = res.data.data;
  } else {
    ElMessage.error('获取产品失败');
  }
};

// 打开弹窗（新增或编辑）
const openDialog = (product = {}) => {
  form.value = {
    id: null,
    name: '',
    price: 0,
    description: '',
    amount: 0,
    imageUrl: '',
    ...product
  };
  dialogVisible.value = true;
};

// 自定义图片上传
const customUpload = async (options) => {
  const { file } = options;
  // 传产品ID和文件给后端，后端从token拿用户，不需要userId
  const res = await uploadProductImage(form.value.id, file);
  if (res.data.code === 200) {
    ElMessage.success('图片上传成功');
    form.value.imageUrl = res.data.data;  // 更新图片预览
    fetchProducts();  // 刷新列表
  } else {
    ElMessage.error(res.data.msg || '上传失败');
  }
};

const submitForm = async () => {
  let res;
  if (form.value.id) {
    // 编辑已有产品，先更新产品信息
    res = await updateProduct(form.value);
  } else {
    // 新增产品
    res = await addProduct(form.value);
    if (res.data.code === 200) {
      // 赋值新增产品id，方便上传图片
      form.value.id = res.data.data?.id || form.value.id;
    }
  }

  if (res.data.code === 200) {
    ElMessage.success('操作成功');

    // 如果表单里有图片文件，且是新增时，要调用上传接口
    if (form.value.imageFile && !form.value.id) {
      // 这里逻辑是先保存产品，再上传图片
      const uploadRes = await uploadProductImage(form.value.id, form.value.imageFile);
      if (uploadRes.data.code === 200) {
        form.value.imageUrl = uploadRes.data.data; // 更新图片预览
      } else {
        ElMessage.error('图片上传失败');
      }
    }

    fetchProducts();
  } else {
    ElMessage.error(res.data.msg || '操作失败');
  }
};



// 删除产品
const deletethisProduct = async (id) => {
  ElMessageBox.confirm('确认删除该产品吗？', '提示', {
    type: 'warning'
  })
      .then(async () => {
        const res = await deleteProduct(id);
        if (res.data.code === 200) {
          ElMessage.success('删除成功');
          fetchProducts();
        } else {
          ElMessage.error('删除失败');
        }
      })
      .catch(() => {});
};

// 搜索产品
const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning('请输入名称进行搜索');
    return;
  }
  const res = await searchProductsByName(searchKeyword.value);
  if (res.data.code === 200) {
    productList.value = res.data.data;
  } else {
    ElMessage.error('搜索失败');
  }
};

// 重置搜索
const resetSearch = () => {
  searchKeyword.value = '';
  fetchProducts();
};

onMounted(() => {
  fetchProducts();
});
</script>

