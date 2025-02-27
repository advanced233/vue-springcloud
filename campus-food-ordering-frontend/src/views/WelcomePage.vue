<template>
  <div class="container">
    <el-card class="welcome-card" shadow="hover">
      <h1>欢迎来到校园订餐平台！</h1>
      <el-space direction="vertical" size="large" class="button-group">
        <el-button type="primary" size="large" @click="goToUserLogin">用户登录</el-button>
        <el-button type="success" size="large" @click="goToUserRegister">用户注册</el-button>
        <el-button type="warning" size="large" @click="goToMerchantLogin">商家登录</el-button>
        <el-button type="danger" size="large" @click="goToMerchantRegister">商家注册</el-button>
      </el-space>
    </el-card>
  </div>
</template>

<script>
export default {
  methods: {
    goToUserLogin() {
      this.$router.push('/user/login');
    },
    goToUserRegister() {
      this.$router.push('/user/register');
    },
    goToMerchantLogin() {
      this.$router.push('/merchant/login');
    },
    goToMerchantRegister() {
      this.$router.push('/merchant/register');
    },
  },
};
</script>

<style scoped>
.container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f4f4f9;
  padding: 20px;
}

.welcome-card {
  width: 400px;
  text-align: center;
  padding: 30px;
  border-radius: 10px;
}

h1 {
  font-size: 24px;
  color: #333;
  margin-bottom: 20px;
}

.button-group {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}
</style>
