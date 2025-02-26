<template>
  <div class="container">
    <div class="page-wrapper">
      <h2>我的订单列表</h2>
      <ul class="order-list">
        <li
            v-for="order in orders"
            :key="order.id"
            class="order-item"
            @click="showOrderDetails(order)"
        >
          <div class="order-summary">
            <p><strong>订单号:</strong> {{ order.id }}</p>
            <p><strong>商家:</strong> {{ order.merchantName }}</p>
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
        <p><strong>商家:</strong> {{ selectedOrder.merchantName }}</p>
        <p><strong>状态:</strong> {{ getStatusText(selectedOrder.status) }}</p>
        <p><strong>下单日期:</strong> {{ formatDate(selectedOrder.createTime) }}</p>
        <div v-if="selectedOrder.orderItems && selectedOrder.orderItems.length">
          <h4>购买菜品：</h4>
          <ul>
            <li v-for="(item, index) in selectedOrder.orderItems" :key="index">
              {{ item.dishName }} x {{ item.quantity }}
            </li>
          </ul>
        </div>
        <button @click="closeModal">关闭</button>
      </div>
    </div>
  </div>
</template>

<script>
// 注意：这里需要用到订单和商家/菜品相关的 API
// 假设在 api/order.js 中新增了 getOrderItems(orderId) 接口
import { getOrdersByUser,
  getOrderItems
} from '../api/order';
import { getMerchant, getDishById } from '../api/merchant';

export default {
  data() {
    return {
      orders: [],           // 用户的所有订单，附加上订单项及商家名称
      message: '',          // 错误或提示信息
      selectedOrder: null,  // 当前选中查看详情的订单
      userId: localStorage.getItem('userId') // 登录后存入 localStorage 的 userId
    };
  },
  created() {
    if (this.userId) {
      this.fetchOrders();
    } else {
      this.message = '用户未登录';
    }
  },
  methods: {
    async fetchOrders() {
      try {
        // 获取用户订单（返回的订单仅包含 id、user_id、merchant_id、status、createTime 等字段）
        const response = await getOrdersByUser(this.userId);
        let orders = response.data;
        // 按下单日期倒序排列
        orders.sort((a, b) => new Date(b.createTime) - new Date(a.createTime));

        // 对每个订单，补充订单项和商家名称
        for (let order of orders) {
          // 获取商家名称（假设 getMerchant 返回一个商家对象，其 name 字段为商家名称）
          try {
            const merchantRes = await getMerchant(order.merchantId);
            order.merchantName = merchantRes.data.name;
          } catch (e) {
            console.error('获取商家信息失败：', e);
            order.merchantName = order.merchantId;
          }

          // 获取订单项（调用 getOrderItems(order.id) 接口，返回数组，每项包含 dish_id 和 quantity）
          try {
            const itemsRes = await getOrderItems(order.id);
            order.orderItems = itemsRes.data;
            // 对每个订单项，根据 dish_id 获取菜品名称
            for (let item of order.orderItems) {
              try {
                const dishRes = await getDishById(item.dishId);
                console.log(dishRes)
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
    // 点击订单显示详情
    showOrderDetails(order) {
      this.selectedOrder = order;
    },
    closeModal() {
      this.selectedOrder = null;
    },
    // 将订单状态数字转换为文本（根据实际业务进行调整）
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
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
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
  background: rgba(0, 0, 0, 0.5);
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
</style>
