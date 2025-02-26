<template>
  <div class="container">
    <div class="page-wrapper">
      <h2>我的订单管理</h2>
      <ul class="order-list">
        <li
            v-for="order in orders"
            :key="order.id"
            class="order-item"
            @click="showOrderDetails(order)"
        >
          <div class="order-summary">
            <p><strong>订单号:</strong> {{ order.id }}</p>
            <p><strong>用户:</strong> {{ order.userId }}</p>
            <p><strong>状态:</strong> {{ getStatusText(order.status) }}</p>
            <p><strong>下单日期:</strong> {{ formatDate(order.createTime) }}</p>
          </div>
        </li>
      </ul>
      <p v-if="message" class="message">{{ message }}</p>
    </div>

    <!-- 订单详情模态框 -->
    <div v-if="selectedOrder" class="modal">
      <div class="modal-content">
        <h3>订单详情</h3>
        <p><strong>订单号:</strong> {{ selectedOrder.id }}</p>
        <p><strong>用户:</strong> {{ selectedOrder.userId }}</p>
        <p><strong>当前状态:</strong> {{ getStatusText(selectedOrder.status) }}</p>
        <p><strong>下单日期:</strong> {{ formatDate(selectedOrder.createTime) }}</p>
        <div v-if="selectedOrder.orderItems && selectedOrder.orderItems.length">
          <h4>购买菜品：</h4>
          <ul>
            <li v-for="(item, index) in selectedOrder.orderItems" :key="index">
              {{ item.dishName }} x {{ item.quantity }}
            </li>
          </ul>
        </div>
        <!-- 更新订单状态区域 -->
        <div class="update-status">
          <label for="order-status">更新订单状态:</label>
          <select id="order-status" v-model="editedStatus">
            <option value="0">待处理</option>
            <option value="1">已接单</option>
            <option value="2">已完成</option>
            <option value="3">已取消</option>
          </select>
          <button @click="updateOrderStatusHandler">更新状态</button>
        </div>
        <button @click="closeModal">关闭</button>
      </div>
    </div>
  </div>
</template>

<script>
// 引入订单和商家/菜品相关的 API
import { getOrdersByMerchant, getOrderItems, updateOrderStatus } from '../api/order';
import { getDishById } from '../api/merchant';

export default {
  data() {
    return {
      orders: [],           // 当前商家的订单列表（附加了订单项数据）
      message: '',          // 提示或错误信息
      selectedOrder: null,  // 当前选中查看详情的订单
      editedStatus: null,   // 用于编辑订单状态的变量
      merchantId: localStorage.getItem('merchantId') // 登录后存入 localStorage 的商家ID
    };
  },
  created() {
    if (this.merchantId) {
      this.fetchOrders();
    } else {
      this.message = '商家未登录';
    }
  },
  methods: {
    async fetchOrders() {
      try {
        // 获取商家订单（返回的订单对象包含 id、userId、merchantId、status、createTime 等字段）
        const response = await getOrdersByMerchant(this.merchantId);
        let orders = response.data;
        // 按下单日期倒序排列
        orders.sort((a, b) => new Date(b.createTime) - new Date(a.createTime));

        // 对每个订单，补充订单项数据，并为每个订单项获取菜品名称
        for (let order of orders) {
          try {
            const itemsRes = await getOrderItems(order.id);
            order.orderItems = itemsRes.data;
            for (let item of order.orderItems) {
              try {
                // 调用 getDishById，根据 dishId 获取菜品名称
                const dishRes = await getDishById(item.dishId);
                // 假设返回的 data 就是菜品名称或包含 name 字段的对象
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
    // 点击订单显示详情，并初始化编辑状态
    showOrderDetails(order) {
      this.selectedOrder = order;
      this.editedStatus = order.status; // 默认选中当前订单状态
    },
    closeModal() {
      this.selectedOrder = null;
    },
    // 将订单状态数字转换为文本显示
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
    // 更新订单状态（调用 updateOrderStatus API）
    async updateOrderStatusHandler() {
      if (this.selectedOrder && this.editedStatus != null && this.editedStatus != this.selectedOrder.status) {
        try {
          await updateOrderStatus(this.selectedOrder.id, this.editedStatus);
          // 更新本地订单状态
          this.selectedOrder.status = this.editedStatus;
          this.message = '订单状态更新成功';
        } catch (error) {
          console.error('更新订单状态失败：', error);
          this.message = '更新订单状态失败，请重试';
        }
      }
    }
  }
};
</script>

<style scoped>
/* 整体布局 */
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
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

h2 {
  margin-bottom: 20px;
  color: #333;
}

/* 订单列表样式 */
.order-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.order-item {
  padding: 15px;
  border-bottom: 1px solid #ddd;
  cursor: pointer;
  transition: background-color 0.2s;
}

.order-item:hover {
  background-color: #f0f0f0;
}

.order-summary p {
  margin: 5px 0;
}

.message {
  margin-top: 15px;
  font-size: 16px;
  color: #ff4d4f;
}

/* 模态框样式 */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-content {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  width: 400px;
  text-align: left;
}

.modal-content h3 {
  margin-top: 0;
}

.modal-content button {
  margin-top: 15px;
  padding: 8px 16px;
  background-color: #007bff;
  border: none;
  color: #fff;
  border-radius: 4px;
  cursor: pointer;
}

.modal-content button:hover {
  background-color: #0056b3;
}

/* 更新状态区域 */
.update-status {
  margin-top: 15px;
  display: flex;
  align-items: center;
}

.update-status label {
  margin-right: 10px;
}
</style>
