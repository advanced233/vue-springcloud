<template>
  <div class="container">
    <el-card class="form-wrapper" shadow="hover">
      <template #header>
        <div class="header-wrapper">
          <el-button type="text" icon="el-icon-back" @click="goBack">返回</el-button>
        </div>
      </template>

      <h2>商家登录</h2>
      <el-form @submit.prevent="handleLogin" label-position="top">
        <el-form-item label="账号">
          <el-input v-model="account" placeholder="请输入账号" clearable />
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
import { loginMerchant } from '../api/merchant';
import { ElMessage } from 'element-plus';

export default {
  data() {
    return {
      account: '',
      password: '',
      message: ''
    };
  },
  methods: {
    async handleLogin() {
      try {
        const response = await loginMerchant(this.account, this.password);
        // 后端返回的对象中包含 merchantId, message, status
        const { merchantId, message, status } = response.data;

        console.log(response.data)

        // 若商家被封禁（status=0），则提示并终止登录
        if (status === 0) {
          this.message = '该商家已被封禁，无法登录';
          ElMessage.error(this.message);
          return;
        }

        // 如果 merchantId 存在且 status !== 0 => 登录成功
        if (merchantId) {
          localStorage.setItem('merchantId', merchantId);
          this.message = message; // 如“登录成功，欢迎 xxx”
          this.$router.push('/merchant/homepage');
        } else {
          // 登录失败
          this.message = message; // 如“商家不存在”或“密码错误”
          ElMessage.error(message);
        }
      } catch (error) {
        console.error(error);
        this.message = '登录失败，请重试。';
        ElMessage.error('登录失败');
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
