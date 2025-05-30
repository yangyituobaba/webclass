// services/order.js
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8000/order';  // 根据实际端口和上下文改

// 获取订单列表，根据用户ID和角色过滤
export function getOrders(username) {
    return axios.post(`${API_BASE_URL}/list`, { username});
}

// 创建订单
export function createOrder(order) {
    return axios.post(`${API_BASE_URL}/create`, order);
}

// 获取订单详情
export function getOrderDetail(id) {
    return axios.post(`${API_BASE_URL}/detail`, { id });
}

// 客户端更新订单状态（如取消订单）
export function updateStatusByClient(orderId, status) {
    return axios.post(`${API_BASE_URL}/update/client`, null, {
        params: { orderId, status }
    });
}

// 配送员更新订单状态
export function updateStatusByDelivery(orderId, status, deliveryId) {
    return axios.post(`${API_BASE_URL}/update/delivery`, null, {
        params: { orderId, status, deliveryId }
    });
}

// 管理员更新订单状态
export function updateStatusByAdmin(orderId, status, deliveryId) {
    return axios.post(`${API_BASE_URL}/update/admin`, null, {
        params: { orderId, status, deliveryId }
    });
}
