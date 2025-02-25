<template>
  <div class="container">
    <div class="form-wrapper">
      <!-- 返回按钮 -->
      <button class="back-button" @click="goBack">← 返回</button>

      <h2>用户登录</h2>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label>用户名:</label>
          <input v-model="username" type="text" placeholder="请输入用户名" required />
        </div>
        <div class="form-group">
          <label>密码:</label>
          <input v-model="password" type="password" placeholder="请输入密码" required />
        </div>
        <button type="submit">登录</button>
      </form>
      <p v-if="message" class="message">{{ message }}</p>
    </div>
  </div>
</template>

<script>
import { loginUser } from '../api/user';

export default {
  data() {
    return {
      username: '',
      password: '',
      message: ''
    };
  },
  methods: {
    async handleLogin() {
      try {
        const response = await loginUser(this.username, this.password);
        // 假设后端返回的数据格式为 { userId: 123, message: '登录成功，欢迎 xxx' }
        const { userId, message } = response.data;
        // 同时存入 localStorage，以便页面刷新后依然保存登录状态
        localStorage.setItem('userId', userId);
        this.message = message;
        // 登录成功后跳转到用户主页
        this.$router.push('/user/homepage');
      } catch (error) {
        console.error(error);
        this.message = '登录失败，请重试。';
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
