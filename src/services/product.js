// services/user.js
import axios from 'axios';

// 后端基础路径，根据你后端启动端口和上下文改
const API_BASE_URL = 'http://localhost:8000/product';

// 获取产品列表
export function getProducts() {
    return axios.get(`${API_BASE_URL}/list`);
}
// 获取某个商品详情
export function getProductDetail(id) {
    return axios.get(`${API_BASE_URL}/${id}`);
}
