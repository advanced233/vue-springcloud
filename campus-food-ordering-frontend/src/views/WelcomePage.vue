<template>
  <div class="container">
    <h1>欢迎来到校园订餐平台！</h1>
    <div class="button-group">
      <button @click="goToUserLogin">用户登录</button>
      <button @click="goToUserRegister">用户注册</button>
      <button @click="goToMerchantLogin">商家登录</button>
      <button @click="goToMerchantRegister">商家注册</button>
    </div>
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
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
  text-align: center;
  background-color: #f4f4f9;
}

h1 {
  font-size: 24px;
  color: #333;
  margin-bottom: 20px;
}

.button-group {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

button {
  width: 200px;
  padding: 10px 20px;
  font-size: 18px;
  color: white;
  background-color: #007bff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s ease, transform 0.2s;
}

button:hover {
  background-color: #0056b3;
  transform: scale(1.05);
}

button:active {
  background-color: #004494;
  transform: scale(0.98);
}
</style>
