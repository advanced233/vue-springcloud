<template>
  <div class="container">
    <div class="checkout-wrapper">
      <h2>订单结算</h2>
      <p v-if="totalPrice > 0">您总计消费：{{ totalPrice }} 元</p>
      <p v-else>您的购物车为空</p>
      <button v-if="totalPrice > 0" @click="confirmPayment">确认支付</button>
      <p v-if="message" class="message">{{ message }}</p>
    </div>
  </div>
</template>

<script>
import { createOrder } from '../api/order';

export default {
  data() {
    return {
      totalPrice: 0,
      orderItems: [],
      message: ''
    };
  },
  created() {
    // 从 localStorage 读取用户购物车（前面页面在加入购物车时应将 cart 存入 localStorage）
    const cartData = localStorage.getItem('cart');
    if (cartData) {
      const cart = JSON.parse(cartData);
      // 计算总价，并转换购物车数据为订单项（orderItems）
      let sum = 0;
      this.orderItems = cart.map(item => {
        sum += item.dish.price * item.quantity;
        return {
          dishId: item.dish.id,  // 确保这里 dish.id 为字符串（如果后端需要字符串，已在后端处理 Long）
          quantity: item.quantity,
          price: item.dish.price
        };
      });
      // 保留两位小数
      this.totalPrice = sum.toFixed(2);
    }
  },
  methods: {
    async confirmPayment() {
      // 获取用户 id（登录后存入 localStorage）
      const userId = localStorage.getItem('userId');
      if (!userId) {
        this.message = '请先登录';
        return;
      }
      // 获取 merchantId 可从路由 query 中获取
      const merchantId = this.$route.query.merchantId;
      if (!merchantId) {
        this.message = '无效的商家ID';
        return;
      }
      // 构造订单对象，注意订单项不能为空
      const order = {
        userId: userId,         // 用户 ID
        merchantId: merchantId, // 商家 ID
        // totalAmount: Number(this.totalPrice), // 总金额
        // status: 0,              // 订单状态：0 待处理
        orderItems: this.orderItems
      };

      // 以 JSON 格式打印订单信息
      console.log("订单请求数据:", JSON.stringify(order, null, 2));

      try {
        const response = await createOrder(order);
        this.message = response.data; // 后端返回 "订单创建成功" 或其他提示
        // 支付成功后，可清空购物车数据
        localStorage.removeItem('cart');
      } catch (error) {
        console.error("订单创建失败:", error);
        this.message = '下单失败，请重试';
      }
    }
  }
};
</script>

<style scoped>
.container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f4f4f9;
}

.checkout-wrapper {
  background: #fff;
  padding: 30px;
  border-radius: 10px;
  width: 400px;
  text-align: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

h2 {
  margin-bottom: 20px;
  color: #333;
}

button {
  margin-top: 20px;
  padding: 10px 20px;
  font-size: 18px;
  color: #fff;
  background-color: #28a745;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

button:hover {
  background-color: #218838;
}

.message {
  margin-top: 15px;
  font-size: 16px;
  color: #ff4d4f;
}
</style>
