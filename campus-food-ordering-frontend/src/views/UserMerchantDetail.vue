<template>
  <div class="container">
    <div class="page-wrapper">
      <h2>商家餐品列表</h2>

      <!-- 菜品卡片区 -->
      <div class="dishes-wrapper">
        <div
            class="dish-card"
            v-for="dish in dishes"
            :key="dish.id"
        >
          <!-- 如果 dish.image 有值，可以使用 <img :src="dish.image" alt="..." class="dish-image" /> -->
          <div class="dish-image">
            <!-- 占位区域 -->
          </div>
          <div class="dish-info">
            <h3>{{ dish.name }}</h3>
            <p>{{ dish.description }}</p>
            <p class="price">价格: {{ dish.price }} 元</p>
            <!-- 加号按钮，点击加入购物车 -->
            <button @click="addToCart(dish)">+</button>
          </div>
        </div>
      </div>

      <!-- 购物车概要，显示总价、跳转到结算页面 -->
      <div class="cart-summary">
        <p>总价: {{ totalPrice }} 元</p>
        <button @click="goToCheckout">购物车</button>
      </div>

      <!-- 错误或提示信息 -->
      <p v-if="message" class="message">{{ message }}</p>
    </div>
  </div>
</template>

<script>
import { getDishesByMerchantId } from '../api/merchant'; // 假设此API已实现

export default {
  data() {
    return {
      dishes: [],    // 存放该商家的所有菜品
      cart: [],      // 购物车（简单示例：数组形式，每项包含 { dish, quantity }）
      message: ''    // 提示消息
    };
  },
  computed: {
    // 计算购物车总价
    totalPrice() {
      // 以单价 × 数量累加
      const sum = this.cart.reduce((acc, item) => {
        return acc + (item.dish.price * item.quantity);
      }, 0);
      // toFixed(2) 保留两位小数
      return sum.toFixed(2);
    }
  },
  created() {
    // 从URL获取 merchantId
    const merchantId = this.$route.query.merchantId;
    if (!merchantId) {
      this.message = '无效的商家ID';
      return;
    }
    // 拉取商家菜品
    this.fetchDishes(merchantId);
  },
  methods: {
    // 调用后端接口，获取菜品列表
    async fetchDishes(merchantId) {
      try {
        const response = await getDishesByMerchantId(merchantId);
        // 假设后端返回的数据格式是一个菜品数组
        this.dishes = response.data;
      } catch (error) {
        console.error(error);
        this.message = '获取菜品列表失败，请重试。';
      }
    },
    // 将选中的菜品加入购物车
    addToCart(dish) {
      // 判断购物车中是否已有该菜品
      const existingItem = this.cart.find(item => item.dish.id === dish.id);
      if (existingItem) {
        // 已存在则数量 +1
        existingItem.quantity++;
      } else {
        // 不存在则push新对象
        this.cart.push({ dish, quantity: 1 });
      }
    },
    // 跳转到结算页面（此处只做占位，后续再实现）
    goToCheckout() {
      // 这里可以跳转到一个结算页面，比如 '/user/checkout'
      // 目前仅做示例，可自行完善
      console.log('TODO: 跳转到结算页面');
      // this.$router.push({ path: '/user/checkout' });
    }
  }
};
</script>

<style scoped>
/* 页面整体居中 */
.container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f4f4f9;
}

/* 页面主体容器 */
.page-wrapper {
  background: #fff;
  padding: 30px;
  border-radius: 10px;
  width: 720px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  text-align: center;
}

/* 标题 */
h2 {
  margin-bottom: 20px;
  color: #333;
}

/* 卡片布局 */
.dishes-wrapper {
  display: flex;
  flex-wrap: wrap;
  gap: 20px; /* 卡片间距 */
  justify-content: center;
}

/* 单个卡片 */
.dish-card {
  width: 220px;
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
  background-color: #fff;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.dish-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

/* 这里可以放置菜品图片或占位 */
.dish-image {
  width: 100%;
  height: 120px;
  background-color: #f0f0f0;
}

/* 菜品信息 */
.dish-info {
  padding: 12px;
}

.dish-info h3 {
  margin: 0 0 8px;
  color: #333;
  font-size: 16px;
}

.dish-info p {
  margin: 0;
  color: #777;
  font-size: 14px;
}

.price {
  margin-top: 8px;
  color: #333;
  font-weight: bold;
}

/* 加入购物车按钮 */
.dish-info button {
  margin-top: 10px;
  background-color: #007bff;
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 6px 12px;
  cursor: pointer;
  transition: background-color 0.3s ease, transform 0.2s;
}

.dish-info button:hover {
  background-color: #0056b3;
  transform: scale(1.02);
}

/* 购物车摘要 */
.cart-summary {
  margin-top: 20px;
}

.cart-summary button {
  margin-left: 10px;
  background-color: #28a745;
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 6px 12px;
  cursor: pointer;
  transition: background-color 0.3s ease, transform 0.2s;
}

.cart-summary button:hover {
  background-color: #218838;
  transform: scale(1.02);
}

/* 提示消息 */
.message {
  margin-top: 15px;
  font-size: 16px;
  color: #ff4d4f;
}
</style>
