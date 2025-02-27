<template>
  <div class="container">
    <div class="page-wrapper">
      <h2>商家餐品列表</h2>

      <!-- 搜索框 -->
      <div class="search-wrapper">
        <el-input
            v-model="searchQuery"
            placeholder="搜索菜品"
            @keyup.enter="searchDishes"
            style="width: 300px; margin-bottom: 20px;"
        />
        <el-button type="primary" @click="searchDishes">搜索</el-button>
      </div>

      <!-- 菜品卡片区 -->
      <div class="dishes-wrapper">
        <div class="dish-card" v-for="dish in dishes" :key="dish.id">
          <div class="dish-image">
            <el-image
                v-if="dish.image"
                :src="dish.image"
                fit="contain"
                class="dish-image-el"
            />
            <span v-else>No Image</span>
          </div>

          <div class="dish-info">
            <h3>{{ dish.name }}</h3>
            <p>{{ dish.description }}</p>
            <p class="price">价格: {{ dish.price }} 元</p>

            <div class="cart-controls">
              <el-button type="danger" size="small" @click="removeFromCart(dish)">-</el-button>
              <span>{{ getQuantity(dish.id) }}</span>
              <el-button type="success" size="small" @click="addToCart(dish)">+</el-button>
            </div>
          </div>
        </div>
      </div>

      <div class="cart-summary">
        <p>总价: {{ totalPrice }} 元</p>
        <el-button type="primary" @click="goToCheckout">结算</el-button>
        <el-button type="primary" @click="viewCart">查看购物车</el-button>
      </div>

      <p v-if="message" class="message">{{ message }}</p>
    </div>
  </div>
</template>

<script>
import { getDishesByMerchantId } from '../api/merchant';

export default {
  data() {
    return {
      dishes: [],           // 商家的所有菜品
      cart: [],             // 购物车
      searchQuery: '',      // 搜索框内容
      message: ''           // 提示信息
    };
  },
  computed: {
    // 计算购物车总价
    totalPrice() {
      const sum = this.cart.reduce((acc, item) => {
        return acc + (item.dish.price * item.quantity);
      }, 0);
      return sum.toFixed(2);
    }
  },
  created() {
    // 从 URL 获取商家ID
    const merchantId = this.$route.query.merchantId;
    if (!merchantId) {
      this.message = '无效的商家ID';
      return;
    }
    this.fetchDishes(merchantId);

    // 从 localStorage 获取购物车数据
    const storedCart = localStorage.getItem('cart');
    if (storedCart) {
      this.cart = JSON.parse(storedCart);
    }
  },
  methods: {
    // 获取所有菜品
    async fetchDishes(merchantId) {
      try {
        const response = await getDishesByMerchantId(merchantId);
        this.dishes = response.data;
      } catch (error) {
        console.error(error);
        this.message = '获取菜品列表失败，请重试。';
      }
    },
    // 搜索菜品
    searchDishes() {
      const query = this.searchQuery.trim().toLowerCase();
      if (!query) {
        // 如果搜索框为空，显示所有菜品
        this.fetchDishes(this.$route.query.merchantId);
      } else {
        // 如果有搜索条件，过滤菜品列表
        this.dishes = this.dishes.filter(dish =>
            dish.name.toLowerCase().includes(query) || dish.description.toLowerCase().includes(query)
        );
      }
    },
    // 加入购物车
    addToCart(dish) {
      const existingItem = this.cart.find(item => item.dish.id.toString() === dish.id.toString());
      if (existingItem) {
        existingItem.quantity++;
      } else {
        this.cart.push({ dish, quantity: 1 });
      }
      localStorage.setItem('cart', JSON.stringify(this.cart));
    },
    // 从购物车中减少菜品数量
    removeFromCart(dish) {
      const existingItem = this.cart.find(item => item.dish.id.toString() === dish.id.toString());
      if (existingItem) {
        existingItem.quantity--;
        if (existingItem.quantity <= 0) {
          this.cart = this.cart.filter(item => item.dish.id.toString() !== dish.id.toString());
        }
      }
      localStorage.setItem('cart', JSON.stringify(this.cart));
    },
    // 获取菜品数量
    getQuantity(dishId) {
      const item = this.cart.find(item => item.dish.id.toString() === dishId.toString());
      return item ? item.quantity : 0;
    },
    // 结算跳转
    goToCheckout() {
      this.$router.push({ path: '/user/checkout', query: { merchantId: this.$route.query.merchantId } });
    },
    // 查看购物车
    viewCart() {
      if (this.cart.length === 0) {
        alert('购物车为空');
      } else {
        const details = this.cart
            .map(item => `${item.dish.name} x ${item.quantity}`)
            .join('\n');
        alert('购物车内容：\n' + details);
      }
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

/* 菜品卡片区 */
.dishes-wrapper {
  display: flex;
  flex-wrap: wrap;
  gap: 20px; /* 卡片间距 */
  justify-content: center;
}

/* 单个菜品卡片 */
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

/* 菜品图片占位 */
.dish-image {
  width: 100%;
  height: 120px;
  background-color: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
}
.dish-image-el {
  width: 100%;
  height: 100%;
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

/* 加减控制区 */
.cart-controls {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 10px;
}

.cart-controls button {
  color: #fff;
  border: none;
  border-radius: 4px;
  width: 30px;
  height: 30px;
  cursor: pointer;
  margin: 0 5px;
  transition: background-color 0.3s;
}

.cart-controls button:hover {
  background-color: #0056b3;
}

.cart-controls span {
  min-width: 20px;
  text-align: center;
}

/* 购物车摘要 */
.cart-summary {
  margin-top: 20px;
}

.cart-summary button {
  margin: 0 5px;
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

/* 搜索框和按钮样式 */
.search-wrapper {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.search-wrapper el-input {
  margin-right: 10px;
}

.search-wrapper el-button {
  margin-left: 10px;
}
</style>
