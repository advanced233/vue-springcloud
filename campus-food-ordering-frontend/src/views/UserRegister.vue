<template>
  <div class="container">
    <el-card class="form-wrapper" shadow="hover">
      <template #header>
        <div class="header-wrapper">
          <el-button type="text" icon="el-icon-back" @click="goBack">返回</el-button>
        </div>
      </template>

      <h2>用户注册</h2>
      <el-form @submit.prevent="handleRegister" label-position="top">
        <el-form-item label="用户名">
          <el-input v-model="user.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="user.password" type="password" placeholder="请输入密码" clearable show-password />
        </el-form-item>
        <el-form-item label="手机号码">
          <el-input v-model="user.phone" type="tel" placeholder="请输入手机号码" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleRegister" class="full-width">注册</el-button>
        </el-form-item>
      </el-form>
      <el-alert v-if="message" :title="message" type="error" show-icon class="alert-message" />
    </el-card>
  </div>
</template>

<script>
import { registerUser } from '../api/user';
import { ElMessage } from 'element-plus';

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
        ElMessage.error('注册失败');
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
