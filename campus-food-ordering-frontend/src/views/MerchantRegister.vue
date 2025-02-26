<template>
  <div class="container">
    <div class="form-wrapper">
      <!-- 返回按钮 -->
      <button class="back-button" @click="goBack">← 返回</button>

      <h2>商家注册</h2>
      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label>账号:</label>
          <input v-model="merchant.account" type="text" placeholder="请输入账号" required />
        </div>
        <div class="form-group">
          <label>密码:</label>
          <input v-model="merchant.password" type="password" placeholder="请输入密码" required />
        </div>
        <div class="form-group">
          <label>商家名称:</label>
          <input v-model="merchant.name" type="text" placeholder="请输入商家名称" required />
        </div>
        <div class="form-group">
          <label>Logo地址:</label>
          <input v-model="merchant.logo" type="url" placeholder="请输入Logo的URL" />
        </div>
        <button type="submit">注册</button>
      </form>
      <p v-if="message" class="message">{{ message }}</p>
    </div>
  </div>
</template>

<script>
import { registerMerchant } from '../api/merchant';

export default {
  data() {
    return {
      merchant: {
        account: '',
        password: '',
        name: '',
        logo: ''
      },
      message: ''
    };
  },
  methods: {
    async handleRegister() {
      try {
        const response = await registerMerchant(this.merchant);
        console.log("订单请求数据:", JSON.stringify(this.merchant, null, 2));
        this.message = response.data;
      } catch (error) {
        console.error(error);
        console.log("请求数据:", JSON.stringify(this.merchant, null, 2));
        this.message = '注册失败，请重试。';
      }
    },
    goBack() {
      this.$router.push('/'); // 返回到欢迎界面
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

/* 表单容器 */
.form-wrapper {
  position: relative;
  background: #fff;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  width: 320px;
  text-align: center;
}

/* 返回按钮样式 */
.back-button {
  position: absolute;
  top: 10px;
  left: 10px;
  background: transparent;
  border: none;
  font-size: 16px;
  color: #007bff;
  cursor: pointer;
  padding: 5px 10px;
  transition: color 0.3s ease, transform 0.2s;
}

.back-button:hover {
  color: #0056b3;
  transform: scale(1.05);
}

.back-button:focus {
  outline: none;
}

/* 标题 */
h2 {
  margin-bottom: 20px;
  color: #333;
}

/* 表单组 */
.form-group {
  margin-bottom: 15px;
  text-align: left;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  color: #555;
}

.form-group input {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 16px;
  transition: border-color 0.3s;
}

.form-group input:focus {
  outline: none;
  border-color: #007bff;
}

/* 按钮样式 */
button[type="submit"] {
  width: 100%;
  padding: 10px;
  font-size: 18px;
  color: #fff;
  background-color: #007bff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s ease, transform 0.2s;
}

button[type="submit"]:hover {
  background-color: #0056b3;
  transform: scale(1.02);
}

button[type="submit"]:active {
  background-color: #004494;
  transform: scale(0.98);
}

/* 提示消息 */
.message {
  margin-top: 15px;
  font-size: 16px;
  color: #ff4d4f;
}
</style>
