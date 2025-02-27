<template>
  <div class="container">
    <el-card class="form-wrapper" shadow="hover">
      <template #header>
        <div class="header-wrapper">
          <el-button type="text" icon="el-icon-back" @click="goBack">返回</el-button>
        </div>
      </template>

      <h2>用户登录</h2>
      <el-form @submit.prevent="handleLogin" label-position="top">
        <el-form-item label="用户名">
          <el-input v-model="username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="密码">
          <el-input
              v-model="password"
              type="password"
              placeholder="请输入密码"
              clearable
              show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" class="full-width"
          >登录</el-button
          >
        </el-form-item>
      </el-form>
      <el-alert
          v-if="message"
          :title="message"
          type="error"
          show-icon
          class="alert-message"
      />
    </el-card>
  </div>
</template>

<script>
import { loginUser } from '../api/user';
import { ElMessage } from 'element-plus';

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
        // 后端返回的数据中，应包含 userId、message、status
        const { userId, message, status } = response.data

        console.log(response)

        // 如果后端告知 status=0，则表示用户已被封禁
        if (status === 0) {
          this.message = '该用户已被封禁，无法登录';
          ElMessage.error(this.message);
          return;
        }

        // 如果 userId 不为空，且 status 不是 0，则表示登录成功
        if (userId) {
          localStorage.setItem('userId', userId);
          this.message = message; // 例如“登录成功，欢迎 xxx”
          this.$router.push({ path: '/user/homepage', query: { userId }});
        } else {
          // 登录失败（userId=null），提示错误信息
          this.message = message; // 例如“用户不存在”或“密码错误”
          ElMessage.error(this.message);
        }
      } catch (error) {
        console.error(error);
        this.message = '登录失败，请重试。';
        ElMessage.error(this.message);
      }
    },
    goBack() {
      this.$router.push('/');
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
  padding: 20px;
}

.form-wrapper {
  width: 350px;
  border-radius: 10px;
  text-align: center;
}

.header-wrapper {
  display: flex;
  justify-content: flex-start;
}

.full-width {
  width: 100%;
}

.alert-message {
  margin-top: 20px;
}
</style>
