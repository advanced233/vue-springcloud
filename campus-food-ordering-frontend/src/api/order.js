// api/order.js
import axios from 'axios';

const BASE_URL = 'http://localhost:8103';

export function createOrder(order) {
    return axios.post(`${BASE_URL}/order/create`, order);
}
