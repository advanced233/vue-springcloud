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

      <!-- 推荐商家卡片 -->
      <div
          v-if="recommendedMerchant"
          class="recommended-container"
          @click="goToMerchant(recommendedMerchant.id)"
      >
        <el-card class="recommended-card" shadow="hover">
          <div class="recommended-header">
            <h3>商家推荐</h3>
          </div>
          <div class="recommended-body">
            <div class="merchant-logo-placeholder">
              <el-image
                  v-if="recommendedMerchant.logo"
                  :src="recommendedMerchant.logo"
                  fit="contain"
                  class="merchant-logo"
              />
              <span v-else>Logo</span>
            </div>
            <div class="merchant-info">
              <h3>{{ recommendedMerchant.name }}</h3>
              <el-text type="info">类型：{{ recommendedMerchant.type }}</el-text>
            </div>
          </div>
        </el-card>
      </div>

      <div style="margin-bottom: 20px;">
        <el-select v-model="selectedType" placeholder="请选择商家类型" style="width: 200px;">
          <el-option label="全部" value="" />
          <el-option v-for="type in merchantTypes" :key="type" :label="type" :value="type" />
        </el-select>
      </div>

      <el-row :gutter="20" justify="center">
        <el-col
            v-for="merchant in filteredMerchants"
            :key="merchant.id"
            :xs="24"
            :sm="12"
            :md="8"
            :lg="6"
        >
          <el-card shadow="hover" class="merchant-item" @click="goToMerchant(merchant.id)">
            <div class="merchant-logo-placeholder">
              <el-image
                  v-if="merchant.logo"
                  :src="merchant.logo"
                  fit="contain"
                  class="merchant-logo"
              />
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
import { listMerchants, getMerchant } from '../api/merchant';
import { getOrdersByUser } from '../api/order';
import { ElMessage } from 'element-plus';

export default {
  data() {
    return {
      merchants: [],
      selectedType: '',
      merchantTypes: ['家常菜', '快餐', '茶饮', '甜点', '小吃'],
      message: '',
      recommendedMerchant: null,
      userId: localStorage.getItem('userId')
    };
  },
  created() {
    this.fetchMerchants();
    this.fetchRecommendedMerchant();
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
    async fetchRecommendedMerchant() {
      if (!this.userId) {
        this.message = '用户未登录，无法获取推荐商家';
        return;
      }
      try {
        const response = await getOrdersByUser(this.userId);
        let orders = response.data;
        if (!orders || orders.length === 0) {
          // 没有订单，则不显示推荐商家
          return;
        }
        // 按下单日期倒序排序
        orders.sort((a, b) => new Date(b.createTime) - new Date(a.createTime));
        const recentOrder = orders[0];
        const merchantRes = await getMerchant(recentOrder.merchantId);
        this.recommendedMerchant = merchantRes.data;
      } catch (error) {
        console.error('获取推荐商家失败：', error);
        this.message = '获取推荐商家失败，请重试。';
        ElMessage.error('获取推荐商家失败');
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

/* 推荐商家卡片样式 */
.recommended-container {
  /* 让推荐卡片在页面居中显示 */
  margin: 0 auto 20px;
  cursor: pointer;
  /* 如果你想让推荐卡片整体宽度更小，可以给它一个 max-width */
  max-width: 350px;
  width: 100%;
}

.recommended-card {
  padding: 10px; /* 缩小内边距 */
  border-radius: 8px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.recommended-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.recommended-header {
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 10px;
  padding-bottom: 5px;
}

.recommended-body {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.recommended-body .merchant-logo-placeholder {
  width: 80px;  /* 减小 Logo 占位 */
  height: 80px;
  margin-bottom: 10px;
}

.recommended-body .merchant-info {
  text-align: center;
}
</style>
