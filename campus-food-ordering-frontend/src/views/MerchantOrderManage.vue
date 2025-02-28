<template>
  <div class="container">
    <div class="page-wrapper">
      <el-button type="text" icon="el-icon-back" @click="goBack">返回</el-button>
      <h2>我的订单管理</h2>

      <!-- 销售额显示 -->
      <div class="sales-summary">
        <p>今日销售额: ¥{{ dailySales.toFixed(2) }}</p>
      </div>

      <!-- 筛选区域 -->
      <div class="filter-container">
        <label for="order-filter">筛选订单:</label>
        <el-select v-model="selectedStatus" placeholder="请选择订单状态" style="width: 200px">
          <el-option label="全部订单" value=""></el-option>
          <el-option label="待处理" value="0"></el-option>
          <el-option label="已接单" value="1"></el-option>
          <el-option label="已完成" value="2"></el-option>
          <el-option label="已取消" value="3"></el-option>
        </el-select>
      </div>

      <ul class="order-list">
        <li
            v-for="order in filteredOrders"
            :key="order.id"
            class="order-item"
            @click="showOrderDetails(order)"
        >
          <el-card class="order-summary">
            <template #header>
              <div class="order-header">
                <span>订单号: {{ order.id }}</span>
              </div>
            </template>
            <el-descriptions :column="1" border>
              <el-descriptions-item label="用户">
                {{ order.userId }}
              </el-descriptions-item>
              <el-descriptions-item label="状态">
                <el-tag :type="statusTagType(order.status)">
                  {{ getStatusText(order.status) }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="下单日期">
                {{ formatDate(order.createTime) }}
              </el-descriptions-item>
              <el-descriptions-item label="订单金额">
                ¥{{ order.totalAmount.toFixed(2) }}
              </el-descriptions-item>
            </el-descriptions>
          </el-card>
        </li>
      </ul>
      <p v-if="message" class="message">{{ message }}</p>
    </div>

    <!-- 订单详情模态框 -->
    <div v-if="selectedOrder" class="modal">
      <div class="modal-content">
        <h3>订单详情</h3>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="订单号">
            {{ selectedOrder.id }}
          </el-descriptions-item>
          <el-descriptions-item label="用户">
            {{ selectedOrder.userId }}
          </el-descriptions-item>
          <el-descriptions-item label="当前状态">
            <el-tag :type="statusTagType(selectedOrder.status)">
              {{ getStatusText(selectedOrder.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="下单日期">
            {{ formatDate(selectedOrder.createTime) }}
          </el-descriptions-item>
          <!-- 新增：显示收货地址 -->
          <el-descriptions-item label="收货地址">
            {{ selectedOrder.address ? selectedOrder.address : '暂无地址' }}
          </el-descriptions-item>
          <!-- 显示订单评价，若无评价则提示“暂无评价” -->
          <el-descriptions-item label="订单评价">
            {{ selectedOrder.comment ? selectedOrder.comment : '暂无评价' }}
          </el-descriptions-item>
        </el-descriptions>

        <div
            v-if="selectedOrder.orderItems && selectedOrder.orderItems.length"
            style="margin-top: 15px;"
        >
          <h4>购买菜品：</h4>
          <ul>
            <li v-for="(item, index) in selectedOrder.orderItems" :key="index">
              {{ item.dishName }} x {{ item.quantity }}
            </li>
          </ul>
        </div>

        <!-- 更新订单状态区域 -->
        <div class="update-status" style="margin-top: 15px;">
          <el-form label-position="left" label-width="120px" inline>
            <el-form-item label="更新订单状态">
              <el-select v-model="editedStatus" placeholder="请选择订单状态" style="width: 150px;">
                <el-option label="待处理" value="0"></el-option>
                <el-option label="已接单" value="1"></el-option>
                <el-option label="已完成" value="2"></el-option>
                <el-option label="已取消" value="3"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="updateOrderStatusHandler">更新状态</el-button>
            </el-form-item>
          </el-form>
        </div>

        <el-button type="primary" @click="closeModal" style="margin-top: 15px;">关闭</el-button>
      </div>
    </div>
  </div>
</template>

<script>
// 引入订单相关 API（商家订单、订单项及订单状态更新接口），以及获取菜品名称的接口
import { getOrdersByMerchant, getOrderItems, updateOrderStatus } from '../api/order';
import { getDishById } from '../api/merchant';

export default {
  data() {
    return {
      orders: [],           // 当前商家的订单列表（附加了订单项数据）
      dailySales: 0,        // 今日销售额
      message: '',          // 提示或错误信息
      selectedOrder: null,  // 当前选中查看详情的订单
      editedStatus: null,   // 用于编辑订单状态的变量
      merchantId: localStorage.getItem('merchantId'), // 登录后存入 localStorage 的商家ID
      selectedStatus: ""    // 筛选条件，空字符串表示显示全部订单
    };
  },
  created() {
    if (this.merchantId) {
      this.fetchOrders();
    } else {
      this.message = '商家未登录';
    }
  },
  computed: {
    // 根据 selectedStatus 筛选订单
    filteredOrders() {
      if (this.selectedStatus === "") {
        return this.orders;
      }
      return this.orders.filter(order => order.status === Number(this.selectedStatus));
    }
  },
  methods: {
    async fetchOrders() {
      try {
        // 获取商家订单
        const response = await getOrdersByMerchant(this.merchantId);
        let orders = response.data;
        // 按下单日期倒序排列（最新订单在前）
        orders.sort((a, b) => new Date(b.createTime) - new Date(a.createTime));
        // 计算今日销售额并处理订单
        this.dailySales = 0;
        for (let order of orders) {
          if (this.isToday(order.createTime) && order.status === 2) {
            this.dailySales += order.totalAmount;
          }
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
      } catch (error) {
        console.error(error);
        this.message = '获取订单失败，请重试。';
      }
    },
    goBack() {
      this.$router.go(-1);
    },
    // 判断订单是否为今天
    isToday(dateStr) {
      const orderDate = new Date(dateStr);
      const today = new Date();
      return orderDate.toDateString() === today.toDateString();
    },
    // 点击订单显示详情，并初始化编辑状态
    showOrderDetails(order) {
      this.selectedOrder = order;
      this.editedStatus = order.status;
    },
    closeModal() {
      this.selectedOrder = null;
    },
    // 将订单状态数字转换为文本
    getStatusText(status) {
      switch (status) {
        case 0:
          return '待处理';
        case 1:
          return '已接单';
        case 2:
          return '已完成';
        case 3:
          return '已取消';
        default:
          return '未知状态';
      }
    },
    // 格式化日期
    formatDate(dateStr) {
      const date = new Date(dateStr);
      return date.toLocaleString();
    },
    // 根据订单状态返回对应的 Element Plus 标签类型
    statusTagType(status) {
      const tagTypeMap = {
        0: "warning",
        1: "primary",
        2: "success",
        3: "danger"
      };
      return tagTypeMap[status] || "info";
    },
    // 更新订单状态（调用 updateOrderStatus API）
    async updateOrderStatusHandler() {
      if (this.editedStatus !== this.selectedOrder.status) {
        try {
          // 更新订单状态
          const updatedOrder = await updateOrderStatus(this.selectedOrder.id, this.editedStatus);

          // 更新本地状态
          this.selectedOrder.status = this.editedStatus;

          // 提示成功消息
          this.message = '订单状态更新成功！';

          // 刷新页面
          this.fetchOrders();

          // 关闭模态框
          this.closeModal();
        } catch (error) {
          console.error('更新订单状态失败：', error);
          this.message = '更新状态失败，请重试。';
        }
      } else {
        this.message = '订单状态未更改。';
      }
    },
  },
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

/* 销售额显示样式 */
.sales-summary {
  margin-bottom: 20px;
  font-size: 18px;
  font-weight: bold;
}

/* 筛选区域样式 */
.filter-container {
  margin-bottom: 20px;
}

/* 订单列表的样式 */
.order-list {
  list-style-type: none;
  padding: 0;
}

.order-item {
  margin-bottom: 15px;
}

/* 订单卡片样式 */
.order-summary {
  padding: 10px;
}

/* 订单详情模态框样式 */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-content {
  background: white;
  padding: 20px;
  width: 60%;
  max-width: 800px;
  box-shadow: 0px 4px 12px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
}

.message {
  color: red;
  font-size: 14px;
  margin-top: 10px;
}

/* 按钮样式 */
.el-button {
  margin-top: 10px;
}
</style>
