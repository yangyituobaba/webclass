// services/user.js
import request from "@/utils/request.js";
import useUserStore from '@/store/modules/userStore.js'
const API_BASE_URL = '/user'; // 统一前缀

// 登录接口，返回 token 和用户信息
export function login(username, password, deviceType = 'WEB') {
    return request.post(
        `${API_BASE_URL}/login`,
        { username, password },
        {
            headers: { 'Device-Type': deviceType },
        }
    );
}

// 注册接口
export function register(userData) {
    return request.post(`${API_BASE_URL}/register`, userData);
}

// 获取当前用户信息接口，使用 token
export function getUserProfile(token) {
    return request.post(
        `${API_BASE_URL}/info`,
        {}, // 请求体为空
        {
            headers: {
                Authorization: token
            },
        }
    ).then(res => res.data);
}

// 获取所有用户
export function fetchAllUsers(token) {
    return request.get(`${API_BASE_URL}/all`, {
        headers: {
            Authorization: token
        }
    });
}



export function updateUser(userData,  currentPassword) {
    const store = useUserStore();
    const token = store.token  // ✅ 正确获取响应式状态中的 token
    return request.put(
        `${API_BASE_URL}/update`,
        {
            ...userData,
            currentPassword
        },
        {
            headers: {
                Authorization: token
            },
        }
    );
}
