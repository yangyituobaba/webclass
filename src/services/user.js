// services/user.js
import axios from 'axios';

// 后端基础路径，根据你后端启动端口和上下文改
const API_BASE_URL = 'http://localhost:8000/user';

export function login(username, password, deviceType = 'WEB') {
    return axios.post(
        `${API_BASE_URL}/login`,
        { username, password },
        {
            headers: {
                'Device-Type': deviceType,
            },
        }
    );
}

// 用户注册接口
export function register(userData) {
    return axios.post(`${API_BASE_URL}/register`, userData);
}

export const getUserProfile = (username) => {
    return axios.post(`${API_BASE_URL}/info`, { username })
        .then(res => res.data)
}

// 更新用户信息
export function updateUser(userData) {
    return axios.put(`${API_BASE_URL}/update`, userData);
}
