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
          <el-button type="primary" @click="handleLogin" class="full-width">登录</el-button>
        </el-form-item>
      </el-form>
      <el-alert v-if="message" :title="message" type="error" show-icon class="alert-message" />
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
        const { merchantId, message } = response.data;

        // 判断后端返回的 merchantId 是否为 null
        if (merchantId) {
          // 登录成功
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
