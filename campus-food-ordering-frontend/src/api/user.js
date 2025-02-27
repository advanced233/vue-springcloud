// api/user.js
import axios from 'axios';

// 根据后端实际地址修改 BASE_URL，例如 http://localhost:8080
const BASE_URL = 'http://localhost:8101';

/**
 * 用户注册
 * @param {Object} user 用户注册信息
 */
export function registerUser(user) {
    return axios.post(`${BASE_URL}/user/register`, user);
}

/**
 * 用户登录（示例）
 * @param {string} username
 * @param {string} password
 */
export function loginUser(username, password) {
    return axios.post(`${BASE_URL}/user/login`, null, {
        params: { username, password }
    });
}

/**
 * 获取用户个人信息（包括地址）
 * @param {Number} userId
 */
export function getUserPersonalInfo(userId) {
    return axios.get(`${BASE_URL}/user/personal-info`, {
        params: { userId }
    });
}

/**
 * 更新用户个人信息（包括地址）
 * @param {Object} personalInfo 用户个人信息 DTO 对象
 */
export function updateUserPersonalInfo(personalInfo) {
    return axios.put(`${BASE_URL}/user/personal-info`, personalInfo);
}
