<template>
  <div class="container">
    <el-card class="page-wrapper" shadow="hover">
      <template #header>
        <div class="header-wrapper">
          <el-button type="text" icon="el-icon-back" @click="goBack">返回</el-button>
          <h2>商家列表</h2>
          <div class="header-buttons">
            <el-button type="primary" @click="goToOrderList" icon="el-icon-tickets">
              订单列表
            </el-button>
            <el-button type="primary" @click="goToPersonalInfo" icon="el-icon-user">
              个人信息
            </el-button>
          </div>
        </div>
      </template>

      <div style="margin-bottom: 20px;">
        <el-select v-model="selectedType" placeholder="请选择商家类型" style="width: 200px;">
          <el-option label="全部" value="" />
          <el-option v-for="type in merchantTypes" :key="type" :label="type" :value="type" />
        </el-select>
      </div>

      <el-row :gutter="20" justify="center">
        <el-col v-for="merchant in filteredMerchants" :key="merchant.id" :xs="24" :sm="12" :md="8" :lg="6">
          <el-card shadow="hover" class="merchant-item" @click="goToMerchant(merchant.id)">
            <div class="merchant-logo-placeholder">
              <el-image v-if="merchant.logo" :src="merchant.logo" fit="contain" class="merchant-logo" />
              <span v-else>Logo</span>
            </div>
            <div class="merchant-info">
              <h3>{{ merchant.name }}</h3>
              <el-text type="info">类型：{{ merchant.type }}</el-text>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-alert v-if="message" :title="message" type="error" show-icon class="alert-message" />
    </el-card>
  </div>
</template>

<script>
import { listMerchants } from '../api/merchant';
import { ElMessage } from 'element-plus';

export default {
  data() {
    return {
      merchants: [],
      selectedType: '',
      merchantTypes: ['家常菜', '快餐', '茶饮', '甜点', '小吃'],
      message: ''
    };
  },
  created() {
    this.fetchMerchants();
  },
  computed: {
    filteredMerchants() {
      if (!this.selectedType) {
        return this.merchants;
      }
      return this.merchants.filter(merchant => merchant.type === this.selectedType);
    }
  },
  methods: {
    async fetchMerchants() {
      try {
        const response = await listMerchants();
        this.merchants = response.data;
      } catch (error) {
        console.error(error);
        this.message = '获取商家列表失败，请重试。';
        ElMessage.error('获取商家列表失败');
      }
    },
    goToMerchant(merchantId) {
      this.$router.push({ path: '/user/merchantdetail', query: { merchantId } });
    },
    goToOrderList() {
      this.$router.push({ path: '/user/orderlist' });
    },
    goToPersonalInfo() {
      this.$router.push({ path: '/user/personalinfo' });
    },
    goBack() {
      this.$router.go(-1);
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
  width: 80%;
  max-width: 900px;
  border-radius: 10px;
}

.header-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-buttons {
  display: flex;
  gap: 10px;
}

.merchant-item {
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.merchant-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.merchant-logo-placeholder {
  width: 100%;
  height: 120px;
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #aaa;
  font-size: 18px;
  border-radius: 8px;
}

.merchant-logo {
  width: 100%;
  height: 100%;
}

.alert-message {
  margin-top: 20px;
}
</style>
