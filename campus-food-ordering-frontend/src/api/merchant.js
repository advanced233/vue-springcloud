import axios from 'axios';

const BASE_URL = 'http://localhost:8102'


// 获取所有商家列表
export function listMerchants() {
    return axios.get(`${BASE_URL}/merchant/list`);
}

// 获取单个商家详情
export function getMerchant(merchantId) {
    return axios.get(`${BASE_URL}/merchant/${merchantId}`);
}

// 获取某个商家的菜品列表
export function getDishesByMerchantId(merchantId) {
    return axios.get(`${BASE_URL}/merchant/${merchantId}/dish`);
}

// 商家注册
export function registerMerchant(merchant) {
    return axios.post(`${BASE_URL}/merchant/register`, merchant);
}

// 商家登录
export function loginMerchant(account, password) {
    return axios.post(`${BASE_URL}/merchant/login`, null, { params: { account, password } });
}

// 更新店铺信息
export function updateMerchant(merchant) {
    return axios.put(`${BASE_URL}/merchant/update`, merchant);
}

// 添加菜品
export function addDish(dish) {
    return axios.post(`${BASE_URL}/merchant/dish/add`, dish);
}

// 修改菜品信息
export function updateDish(dish) {
    return axios.put(`${BASE_URL}/merchant/dish/update`, dish);
}

// 修改菜品状态（上架/下架）
export function changeDishStatus(dishId, status) {
    return axios.put(`${BASE_URL}/merchant/dish/status`, null, { params: { dishId, status } });
}

// 删除菜品接口
export function deleteDish(dishId) {
    return axios.delete(`${BASE_URL}/merchant/dish/delete`, { params: { dishId } });
}

// 根据菜品id查询菜品
export function getDishById(dishId) {
    return axios.get(`${BASE_URL}/merchant/dish/${dishId}`);
}


