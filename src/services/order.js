import request from "@/utils/request.js";

const API_BASE_URL = '/order'; // 统一前缀

// 获取订单列表，后端从请求属性取用户名，这里不传用户名
export function getOrders(token) {
    return request.post(
        `${API_BASE_URL}/list`,
        {},
        {
            headers: {
                Authorization: token
            }
        }).then(res=>res.data);
}
// 创建订单
export function createOrder(order,token) {
    return request.post(
        `${API_BASE_URL}/create`,
        order,
        {
            headers: {
                Authorization: token
            }
        }
    );
}

// 获取订单详情，传 id
export function getOrderDetail(id,token) {
    return request.post(
        `${API_BASE_URL}/detail`,
        { id },
        {
            headers: {
                Authorization: token
            }
        }
    );
}

// 客户端更新订单状态，使用请求体传参数更规范
export function updateStatusByClient(orderId, status) {
    return request.post(`${API_BASE_URL}/update/client`, { orderId, status });
}

// 配送员更新订单状态
export function updateStatusByDelivery(orderId, status, deliveryId) {
    return request.post(`${API_BASE_URL}/update/delivery`, { orderId, status, deliveryId });
}

// 管理员更新订单状态
export function updateStatusByAdmin(orderId, status, deliveryId) {
    return request.post(`${API_BASE_URL}/update/admin`, { orderId, status, deliveryId });
}

// 管理员获取所有订单
export function getAllOrders(token) {
    return request.get(
        `${API_BASE_URL}/all`,
        {
            headers: {
                Authorization: token
            }
        }
    ).then(res => res.data);
}


// 根据商品名称关键词模糊搜索订单（适用于所有人）
// 模糊搜索订单（公开接口，不需要 token）
export function searchOrdersByKeyword(keyword) {
    return request.get(`/order/search`, {
        params: { keyword }
    }).then(res => res.data);
}
