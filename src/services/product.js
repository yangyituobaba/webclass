// services/user.js
import request from "@/utils/request.js";
import useUserStore from '@/store/modules/userStore.js'

const API_BASE_URL = '/product'; // 统一前缀


// 获取产品列表
export function getProducts() {
    return request.get(`${API_BASE_URL}/list`);
}

// 获取某个商品详情
export function getProductDetail(id) {
    return request.get(`${API_BASE_URL}/${id}`);
}

// 根据名称搜索商品（模糊查询）
export function searchProductsByName(name) {
    return request.get(`${API_BASE_URL}/search`, {
        params: { name }
    });
}

// 更新产品（需要 PRIMARY 权限）
export function updateProduct(data) {
    const { token } = useUserStore();
    return request.put(`${API_BASE_URL}/update`, data, {
        headers: { Authorization: token }
    });
}

// 删除产品（需要 PRIMARY 权限）
export function deleteProduct(id) {
    const { token } = useUserStore();
    return request.delete(`${API_BASE_URL}/delete/${id}`, {
        headers: { Authorization: token }
    });
}
export function addProduct(data) {
    const { token } = useUserStore();
    return request.post(`${API_BASE_URL}/add`, data, {
        headers: { Authorization: token }
    });
}

export function uploadProductImage(id, file) {
    const { token } = useUserStore();
    const formData = new FormData();
    if (id) {
        formData.append('id', id);
    }
    formData.append('file', file);

    return request.post(`${API_BASE_URL}/upimage`, formData, {
        headers: {
            Authorization: token,
            'Content-Type': 'multipart/form-data'
        }
    });
}


