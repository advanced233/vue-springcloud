<template>
  <div class="container">
    <el-card class="home-card" shadow="hover">
      <h1>管理员主页</h1>
      <el-space direction="vertical" size="large" class="button-group">
        <el-button type="primary" size="large" @click="goToUserManage">用户管理</el-button>
        <el-button type="success" size="large" @click="goToMerchantManage">商家管理</el-button>
      </el-space>
    </el-card>
  </div>
</template>

<script>
export default {
  methods: {
    goToUserManage() {
      this.$router.push('/admin/usermanage');
    },
    goToMerchantManage() {
      this.$router.push('/admin/merchantmanage');
    }
  }
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

.home-card {
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
