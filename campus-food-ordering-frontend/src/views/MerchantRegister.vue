<template>
  <div class="container">
    <el-card class="form-wrapper" shadow="hover">
      <template #header>
        <div class="header-wrapper">
          <el-button type="text" icon="el-icon-back" @click="goBack">返回</el-button>
        </div>
      </template>

      <h2>商家注册</h2>
      <!-- 注意：@submit.prevent可以阻止默认提交，确保走Vue方法 -->
      <el-form @submit.prevent="handleRegister" label-position="top">

        <!-- 账号 -->
        <el-form-item label="账号">
          <el-input
              v-model="merchant.account"
              placeholder="请输入账号"
              clearable
          />
        </el-form-item>

        <!-- 密码 -->
        <el-form-item label="密码">
          <el-input
              v-model="merchant.password"
              type="password"
              placeholder="请输入密码"
              clearable
              show-password
          />
        </el-form-item>

        <!-- 商家名称 -->
        <el-form-item label="商家名称">
          <el-input
              v-model="merchant.name"
              placeholder="请输入商家名称"
              clearable
          />
        </el-form-item>

        <!-- Logo地址 -->
        <el-form-item label="Logo地址">
          <el-input
              v-model="merchant.logo"
              type="url"
              placeholder="请输入Logo的URL"
              clearable
          />
        </el-form-item>

        <!-- 商家类型（下拉框） -->
        <el-form-item label="商家类型">
          <el-select
              v-model="merchant.type"
              placeholder="请选择商家类型"
              clearable
          >
            <el-option
                v-for="option in merchantTypes"
                :key="option"
                :label="option"
                :value="option"
            />
          </el-select>
        </el-form-item>

        <!-- 注册按钮 -->
        <el-form-item>
          <el-button
              type="primary"
              @click="handleRegister"
              class="full-width"
          >
            注册
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 错误或提示信息 -->
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
import { registerMerchant } from '../api/merchant';
import { ElMessage } from 'element-plus';

export default {
  name: 'RegisterMerchant',
  data() {
    return {
      merchant: {
        account: '',
        password: '',
        name: '',
        logo: '',
        type: '' // 新增字段：商家类型
      },
      // 商家类型可选项（可根据实际情况调整或从后端获取）
      merchantTypes: ['家常菜', '快餐', '茶饮', '甜点', '小吃'],

      message: ''
    };
  },
  methods: {
    async handleRegister() {
      try {
        // 将表单中的商家信息提交到后端
        const response = await registerMerchant(this.merchant);
        // 后端返回的消息或结果
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
