<template>
  <div class="container">
    <div class="page-wrapper">
      <el-button type="text" icon="el-icon-back" @click="goBack">返回</el-button>
      <h2>商户管理</h2>

      <!-- 显示所有商户的销售总额 -->
      <div class="total-sales">
        <h3>总销售额: ¥{{ totalSales.toFixed(2) }}</h3>
      </div>

      <!-- 商户列表 -->
      <ul class="merchant-list">
        <li
            v-for="merchant in merchants"
            :key="merchant.id"
            class="merchant-item"
        >
          <el-card class="merchant-summary">
            <template #header>
              <div class="merchant-header">
                <span>商户ID: {{ merchant.id }}</span>
              </div>
            </template>

            <el-descriptions :column="1" border>
              <el-descriptions-item label="商户名称">
                {{ merchant.name }}
              </el-descriptions-item>
              <!-- 显示销售额 -->
              <el-descriptions-item label="销售额">
                ¥{{ merchant.sales ? merchant.sales.toFixed(2) : '0.00' }}
              </el-descriptions-item>
              <el-descriptions-item label="状态">
                <el-tag :type="statusTagType(merchant.status)">
                  {{ getStatusText(merchant.status) }}
                </el-tag>
              </el-descriptions-item>
            </el-descriptions>

            <!-- 操作按钮 -->
            <div class="merchant-actions">
              <el-button
                  v-if="merchant.status !== 0"
                  type="danger"
                  size="small"
                  @click="banMerchant(merchant)"
              >
                封禁
              </el-button>
              <el-button
                  v-else
                  type="success"
                  size="small"
                  @click="unbanMerchant(merchant)"
              >
                解禁
              </el-button>
            </div>
          </el-card>
        </li>
      </ul>

      <!-- 提示信息 -->
      <p v-if="message" class="message">{{ message }}</p>
    </div>
  </div>
</template>

<script>
import { getAllMerchants, updateMerchantStatus } from '../api/merchant';
import { getOrdersByMerchant } from '../api/order';

export default {
  name: 'AdminMerchantManage',
  data() {
    return {
      merchants: [],    // 所有商户信息
      message: '',      // 提示信息
      totalSales: 0,    // 所有商户的销售总额
    };
  },
  created() {
    this.fetchMerchants();
  },
  methods: {
    // 获取所有商户信息，并计算每个商户的销售额（仅累计状态为已完成的订单），最后按销售额降序排序
    async fetchMerchants() {
      try {
        const response = await getAllMerchants();
        this.merchants = response.data || [];

        let totalSales = 0;  // 初始化总销售额

        // 对每个商户查询其所有订单并累计销售额（仅统计状态为2的订单）
        for (let merchant of this.merchants) {
          try {
            const ordersResponse = await getOrdersByMerchant(merchant.id);
            const orders = ordersResponse.data || [];
            let sales = 0;
            orders.forEach(order => {
              if (order.status === 2) {
                sales += order.totalAmount;
              }
            });
            merchant.sales = sales;

            // 累加到总销售额
            totalSales += sales;
          } catch (error) {
            console.error(`获取商户 ${merchant.id} 的订单失败：`, error);
            merchant.sales = 0;
          }
        }

        // 更新总销售额
        this.totalSales = totalSales;

        // 按销售额从大到小排序
        this.merchants.sort((a, b) => b.sales - a.sales);
      } catch (error) {
        console.error(error);
        this.message = '获取商户列表失败，请稍后重试。';
      }
    },
    goBack() {
      this.$router.go(-1);
    },
    // 将商户状态转换为可读文本
    getStatusText(status) {
      if (status === 0) return '已封禁';
      if (status === 1) return '正常';
      return '未知状态';
    },
    // 根据商户状态返回 Tag 的颜色
    statusTagType(status) {
      if (status === 0) return 'danger';  // 已封禁 - 红色
      if (status === 1) return 'success'; // 正常 - 绿色
      return 'info';
    },
    // 封禁商户（status=0）
    async banMerchant(merchant) {
      if (!confirm(`确定要封禁商户【${merchant.name}】吗？`)) {
        return;
      }
      try {
        const response = await updateMerchantStatus(merchant.id, 0);
        this.message = response.data;
        merchant.status = 0;
      } catch (error) {
        console.error(error);
        this.message = '封禁失败，请稍后重试。';
      }
    },
    // 解禁商户（status=1）
    async unbanMerchant(merchant) {
      if (!confirm(`确定要解禁商户【${merchant.name}】吗？`)) {
        return;
      }
      try {
        const response = await updateMerchantStatus(merchant.id, 1);
        this.message = response.data;
        merchant.status = 1;
      } catch (error) {
        console.error(error);
        this.message = '解禁失败，请稍后重试。';
      }
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

/* 显示总销售额 */
.total-sales {
  margin-bottom: 20px;
}

.el-button {
  margin-bottom: 15px;
}

/* 商户列表 */
.merchant-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.merchant-item {
  margin-bottom: 20px;
}

.merchant-summary {
  padding: 15px;
}

.merchant-header {
  font-weight: 500;
  font-size: 16px;
}

.merchant-actions {
  margin-top: 10px;
  text-align: right;
}

.message {
  margin-top: 15px;
  font-size: 16px;
  color: #ff4d4f;
}
</style>
