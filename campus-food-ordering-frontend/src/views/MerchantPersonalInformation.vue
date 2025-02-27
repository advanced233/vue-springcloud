<template>
  <div class="container">
    <div class="page-wrapper">
      <h2>商家信息</h2>
      <!-- 日销售额显示 -->
      <div class="daily-sales">
        <h3>今日销售额: ¥{{ dailySales }}</h3>
      </div>

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
import { getOrdersByMerchant, getOrderItems } from '../api/order';
import { getDishById } from '../api/merchant';

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
      message: '',
      orders: [],           // 当前商家的订单列表（附加了订单项数据）
      dailySales: 0,        // 今日销售额
      merchantId: localStorage.getItem('merchantId') // 登录后存入 localStorage 的商家ID
    };
  },
  created() {
    const merchantId = localStorage.getItem('merchantId');
    if (merchantId) {
      this.fetchMerchantInfo(merchantId);
      this.fetchOrders(); // 获取订单
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
            data.password = '';
            this.form = data;
          })
          .catch(error => {
            console.error(error);
            this.message = '获取商家信息失败';
          });
    },

    // 获取订单列表
    async fetchOrders() {
      try {
        const response = await getOrdersByMerchant(this.merchantId);
        let orders = response.data;
        orders.sort((a, b) => new Date(b.createTime) - new Date(a.createTime));

        // 补充订单项数据
        for (let order of orders) {
          try {
            const itemsRes = await getOrderItems(order.id);
            order.orderItems = itemsRes.data;
            for (let item of order.orderItems) {
              try {
                const dishRes = await getDishById(item.dishId);
                item.dishName = dishRes.data;
              } catch (err) {
                console.error('获取菜品信息失败：', err);
                item.dishName = '未知菜品';
              }
            }
          } catch (err) {
            console.error('获取订单项失败：', err);
            order.orderItems = [];
          }
        }
        this.orders = orders;
        this.calculateDailySales();  // 获取订单后计算今日销售额
      } catch (error) {
        console.error(error);
        this.message = '获取订单失败，请重试。';
      }
    },

    // 计算今日销售额
    calculateDailySales() {
      const today = new Date().toLocaleDateString(); // 获取今天的日期 (格式化为 yyyy/mm/dd)
      let totalSales = 0;

      // 计算所有今天已完成的订单的总销售额
      this.orders.forEach(order => {
        if (this.formatDate(order.createTime) === today && order.status === 2) {
          order.orderItems.forEach(item => {
            totalSales += item.price * item.quantity; // 假设每个 item 有 price 和 quantity 字段
          });
        }
      });

      this.dailySales = totalSales.toFixed(2); // 设置为保留两位小数的格式
    },

    // 格式化日期，简化成 yyyy/mm/dd 格式
    formatDate(dateStr) {
      const date = new Date(dateStr);
      return date.toLocaleDateString();
    },

    // 保存商家信息
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

/* 日销售额显示 */
.daily-sales {
  margin-bottom: 20px;
  font-size: 20px;
  font-weight: bold;
  color: #2c3e50;
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
