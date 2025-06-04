// services/user.js
import request from "@/utils/request.js";

const API_BASE_URL = '/product'; // 统一前缀


// 获取产品列表
export function getProducts() {
    return request.get(`${API_BASE_URL}/list`);
}

// 获取某个商品详情
export function getProductDetail(id) {
    return request.get(`${API_BASE_URL}/${id}`);
}