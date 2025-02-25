<template>
  <div class="container">
    <div class="page-wrapper">
      <h2>商家列表</h2>
      <ul class="merchant-list">
        <li
            v-for="merchant in merchants"
            :key="merchant.id"
            class="merchant-item"
            @click="goToMerchant(merchant.id)"
        >
          <div class="merchant-card">
            <!-- 如果有商家 logo，可以在这里展示
                 例如：<img :src="merchant.logo" alt="商家logo" class="merchant-logo" />
                 没有时可使用占位图或背景色 -->
            <div class="merchant-logo-placeholder">
              <span>Logo</span>
            </div>
            <div class="merchant-info">
              <h3>{{ merchant.name }}</h3>
              <p>店铺介绍（可选）</p>
            </div>
          </div>
        </li>
      </ul>

      <p v-if="message" class="message">{{ message }}</p>
    </div>
  </div>
</template>

<script>
import { listMerchants } from '../api/merchant';

export default {
  data() {
    return {
      merchants: [],
      message: ''
    };
  },
  created() {
    this.fetchMerchants();
  },
  methods: {
    async fetchMerchants() {
      try {
        const response = await listMerchants();
        this.merchants = response.data; // 后端返回的商家数组
      } catch (error) {
        console.error(error);
        this.message = '获取商家列表失败，请重试。';
      }
    },
    goToMerchant(merchantId) {
      this.$router.push({ path: '/user/merchantdetail', query: { merchantId } });
    }
  }
};
</script>

<style scoped>
/* 整体布局 */
.container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f4f4f9;
}

.page-wrapper {
  background: #fff;
  padding: 30px;
  border-radius: 10px;
  width: 720px; /* 可以根据需要扩大显示区域 */
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  text-align: center;
}

h2 {
  margin-bottom: 20px;
  color: #333;
}

/* 列表布局：flex 布局 + 卡片 */
.merchant-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;          /* 使用 flex 布局 */
  flex-wrap: wrap;        /* 宽度不足时自动换行 */
  justify-content: center;
  gap: 20px;              /* 卡片之间的间距 */
}

.merchant-item {
  width: 220px;           /* 卡片宽度，可根据需求调整 */
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.merchant-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

/* 卡片主体 */
.merchant-card {
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;        /* 隐藏边缘超出部分 */
  border: 1px solid #ddd;
  text-align: center;
}

/* 如果有实际商家 logo，请在这里用 <img> 标签替换 */
.merchant-logo-placeholder {
  width: 100%;
  height: 120px;
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #aaa;
  font-size: 18px;
}

.merchant-info {
  padding: 12px;
}

.merchant-info h3 {
  margin: 0 0 8px;
  color: #333;
}

.merchant-info p {
  margin: 0;
  color: #777;
}

/* 提示消息 */
.message {
  margin-top: 15px;
  font-size: 16px;
  color: #ff4d4f;
}
</style>
