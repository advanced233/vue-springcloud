// api/order.js
import axios from 'axios';

const BASE_URL = 'http://localhost:8103';

// 用户下单
export function createOrder(order) {
    return axios.post(`${BASE_URL}/order/create`, order);
}

// 商家更新订单状态，例如将订单状态更新为已接单
export function updateOrderStatus(orderId, status) {
    return axios.put(`${BASE_URL}/order/${orderId}/status`, null, { params: { status } });
}

// 用户查看订单历史
export function getOrdersByUser(userId) {
    return axios.get(`${BASE_URL}/order/user/${userId}`);
}

// 商家查看订单历史
export function getOrdersByMerchant(merchantId) {
    return axios.get(`${BASE_URL}/order/merchant/${merchantId}`);
}

export function getOrderItems(orderId) {
    return axios.get(`${BASE_URL}/order/${orderId}/items`);
}

// 用户评论订单
export function updateComment(orderId, comment) {
    return axios.put(`${BASE_URL}/order/${orderId}/comment`, null, { params: { comment } });
}