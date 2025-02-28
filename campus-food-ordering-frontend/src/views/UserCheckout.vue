<template>
  <div class="container">
    <div class="checkout-wrapper">
      <h2>订单结算</h2>
      <p v-if="totalPrice > 0">您总计消费：{{ totalPrice }} 元</p>
      <p v-else>您的购物车为空</p>

      <!-- 显示倒计时 -->
      <p v-if="countdown > 0">倒计时: {{ formatTime(countdown) }}</p>
      <p v-else v-if="!isOrderConfirmed">创建订单失败，您未及时确认支付。</p>

      <button v-if="totalPrice > 0 && countdown > 0" @click="confirmPayment">确认支付</button>

      <p v-if="message" class="message">{{ message }}</p>
    </div>
  </div>
</template>

<script>
import { createOrder } from '../api/order';

export default {
  data() {
    return {
      totalPrice: 0,         // 总价
      orderItems: [],        // 订单项
      message: '',           // 错误或成功信息
      countdown: 10,        // 倒计时：600秒，即10分钟
      isOrderConfirmed: false, // 标识是否已确认支付
      countdownTimer: null   // 用于存储 setInterval 定时器
    };
  },
  created() {
    // 从 localStorage 获取购物车数据并计算总价
    const cartData = localStorage.getItem('cart');
    if (cartData) {
      const cart = JSON.parse(cartData);
      let sum = 0;
      this.orderItems = cart.map(item => {
        sum += item.dish.price * item.quantity;
        return {
          dishId: item.dish.id,
          quantity: item.quantity,
          price: item.dish.price
        };
      });
      this.totalPrice = sum.toFixed(2);
    }

    // 启动倒计时
    this.startCountdown();
  },
  beforeDestroy() {
    // 清理定时器
    if (this.countdownTimer) {
      clearInterval(this.countdownTimer);
    }
  },
  methods: {
    // 格式化倒计时（转换为分钟:秒）
    formatTime(seconds) {
      const minutes = Math.floor(seconds / 60);
      const secs = seconds % 60;
      return `${minutes}:${secs < 10 ? '0' + secs : secs}`;
    },

    // 启动倒计时
    startCountdown() {
      this.countdownTimer = setInterval(() => {
        if (this.countdown > 0) {
          this.countdown--;
        } else {
          // 倒计时结束，未支付则显示失败信息
          clearInterval(this.countdownTimer);
          if (!this.isOrderConfirmed) {
            this.message = '创建订单失败，您未及时确认支付。';
          }
        }
      }, 1000);
    },

    // 确认支付
    async confirmPayment() {
      const userId = localStorage.getItem('userId');
      if (!userId) {
        this.message = '请先登录';
        return;
      }

      const merchantId = this.$route.query.merchantId;
      if (!merchantId) {
        this.message = '无效的商家ID';
        return;
      }

      const order = {
        userId: userId,
        merchantId: merchantId,
        orderItems: this.orderItems
      };

      console.log("订单请求数据:", JSON.stringify(order, null, 2));

      try {
        const response = await createOrder(order);
        this.message = response.data;  // 后端返回 "订单创建成功" 或其他提示
        this.isOrderConfirmed = true;  // 标记订单已确认支付
        localStorage.removeItem('cart'); // 支付成功后清空购物车

        // 确认支付后延时显示成功消息，并跳转回上一页面
        setTimeout(() => {
          this.message = '支付成功！';
          setTimeout(() => {
            this.$router.go(-1);  // 这一行会使页面返回到上一页
          }, 2000);  // 2秒后跳转
        }, 500);  // 0.5秒后显示“支付成功”消息
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

p {
  font-size: 16px;
  color: #333;
}
</style>
