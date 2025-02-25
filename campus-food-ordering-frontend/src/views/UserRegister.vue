<template>
  <div class="container">
    <div class="form-wrapper">
      <!-- 返回按钮 -->
      <button class="button" @click="goBack">← 返回</button>

      <h2>用户注册</h2>
      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label>用户名:</label>
          <input v-model="user.username" type="text" placeholder="请输入用户名" required />
        </div>
        <div class="form-group">
          <label>密码:</label>
          <input v-model="user.password" type="password" placeholder="请输入密码" required />
        </div>
        <div class="form-group">
          <label>手机号码:</label>
          <input v-model="user.phone" type="tel" placeholder="请输入手机号码" />
        </div>
        <button type="submit">注册</button>
      </form>
      <p v-if="message" class="message">{{ message }}</p>
    </div>
  </div>
</template>

<script>
import { registerUser } from '../api/user';

export default {
  data() {
    return {
      user: {
        username: '',
        password: '',
        phone: ''
      },
      message: ''
    };
  },
  methods: {
    async handleRegister() {
      try {
        const response = await registerUser(this.user);
        this.message = response.data;
      } catch (error) {
        console.error(error);
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
  background: transparent; /* 设置背景透明 */
  border: none;
  font-size: 16px;
  color: #007bff;
  cursor: pointer;
  padding: 5px 10px;
  transition: color 0.3s ease, transform 0.2s;
}

.back-button:focus {
  outline: none; /* 去掉点击时的默认轮廓 */
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
button {
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

button:hover {
  background-color: #0056b3;
  transform: scale(1.02);
}

button:active {
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
