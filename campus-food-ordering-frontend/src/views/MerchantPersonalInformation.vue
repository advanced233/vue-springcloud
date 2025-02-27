<template>
  <div class="container">
    <div class="page-wrapper">
      <h2>商家信息</h2>
      <el-form :model="form" label-width="100px">
        <!-- Logo -->
        <el-form-item label="Logo">
          <div class="logo-container">
            <el-input v-model="form.logo" placeholder="请输入Logo URL" />
            <img v-if="form.logo" :src="form.logo" alt="Logo" class="logo-preview" />
          </div>
        </el-form-item>

        <!-- 账号（只读） -->
        <el-form-item label="账号">
          <el-input v-model="form.account" disabled />
        </el-form-item>

        <!-- 名称 -->
        <el-form-item label="商家名称">
          <el-input v-model="form.name" />
        </el-form-item>

        <!-- 密码（只显示空，若商家想修改可输入新密码） -->
        <el-form-item label="密码">
          <el-input
              type="password"
              v-model="form.password"
              placeholder="若不修改，请留空"
          />
        </el-form-item>

        <!-- 保存按钮 -->
        <el-form-item>
          <el-button type="primary" @click="saveMerchantInfo">保存</el-button>
        </el-form-item>
      </el-form>
      <p v-if="message" class="message">{{ message }}</p>
    </div>
  </div>
</template>

<script>
import { getMerchant, updateMerchantInfo } from '../api/merchant';

export default {
  name: 'MerchantPersonalInformation',
  data() {
    return {
      form: {
        id: null,
        account: '',
        password: '',
        name: '',
        logo: ''
      },
      message: ''
    };
  },
  created() {
    const merchantId = localStorage.getItem('merchantId');
    if (merchantId) {
      this.fetchMerchantInfo(merchantId);
    } else {
      this.message = '商家未登录';
    }
  },
  methods: {
    // 获取商家个人信息
    fetchMerchantInfo(merchantId) {
      getMerchant(merchantId)
          .then(response => {
            let data = response.data || {};
            // 不显示加密后的密码
            data.password = '';
            this.form = data;
          })
          .catch(error => {
            console.error(error);
            this.message = '获取商家信息失败';
          });
    },
    // 保存更新后的商家信息
    saveMerchantInfo() {
      updateMerchantInfo(this.form)
          .then(response => {
            this.message = response.data;
          })
          .catch(error => {
            console.error(error);
            this.message = '更新商家信息失败';
          });
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

.page-wrapper {
  background: #fff;
  padding: 30px;
  border-radius: 10px;
  width: 720px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

h2 {
  margin-bottom: 20px;
  color: #333;
}

/* Logo区域 */
.logo-container {
  display: flex;
  align-items: center;
}

.logo-preview {
  width: 75px; /* 设置Logo的宽度 */
  height: 75px; /* 设置Logo的高度 */
  border-radius: 50%;
  margin-left: 10px;
}

/* 提示信息 */
.message {
  margin-top: 15px;
  font-size: 16px;
  color: #ff4d4f;
}
</style>
