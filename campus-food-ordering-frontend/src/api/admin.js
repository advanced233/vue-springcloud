// api/admin.js
import axios from 'axios';

// 根据后端实际地址修改 BASE_URL，例如 http://localhost:8104
const BASE_URL = 'http://localhost:8104';

/**
 * 管理员注册
 * @param {Object} admin 管理员注册信息
 */
export function registerAdmin(admin) {
    return axios.post(`${BASE_URL}/admin/register`, admin);
}

/**
 * 管理员登录
 * @param {string} username
 * @param {string} password
 */
export function loginAdmin(username, password) {
    return axios.post(`${BASE_URL}/admin/login`, null, {
        params: { username, password }
    });
}
