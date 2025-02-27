<template>
  <div class="container">
    <el-card class="form-wrapper" shadow="hover">
      <template #header>
        <div class="header-wrapper">
          <el-button type="text" icon="el-icon-back" @click="goBack">返回</el-button>
        </div>
      </template>

      <h2>商家注册</h2>
      <el-form @submit.prevent="handleRegister" label-position="top">
        <el-form-item label="账号">
          <el-input v-model="merchant.account" placeholder="请输入账号" clearable />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="merchant.password" type="password" placeholder="请输入密码" clearable show-password />
        </el-form-item>
        <el-form-item label="商家名称">
          <el-input v-model="merchant.name" placeholder="请输入商家名称" clearable />
        </el-form-item>
        <el-form-item label="Logo地址">
          <el-input v-model="merchant.logo" type="url" placeholder="请输入Logo的URL" clearable />
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
import { registerMerchant } from '../api/merchant';
import { ElMessage } from 'element-plus';

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
