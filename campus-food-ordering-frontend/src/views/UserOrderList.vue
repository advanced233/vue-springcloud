<template>
  <div class="container">
    <div class="page-wrapper">
      <el-button type="text" icon="el-icon-back" @click="goBack">返回</el-button>
      <h2>我的订单列表</h2>

      <!-- 筛选订单状态的下拉框 -->
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
              <el-descriptions-item label="商家">
                {{ order.merchantName }}
              </el-descriptions-item>
              <el-descriptions-item label="状态">
                <el-tag :type="statusTagType(order.status)">
                  {{ getStatusText(order.status) }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="下单日期">
                {{ formatDate(order.createTime) }}
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
        <p><strong>订单号:</strong> {{ selectedOrder.id }}</p>
        <p><strong>商家:</strong> {{ selectedOrder.merchantName }}</p>
        <p>
          <strong>状态:</strong>
          <span :class="statusClass(selectedOrder.status)">
            {{ getStatusText(selectedOrder.status) }}
          </span>
        </p>
        <p><strong>下单日期:</strong> {{ formatDate(selectedOrder.createTime) }}</p>
        <p><strong>订单金额:</strong> {{ selectedOrder.totalAmount }}</p>
        <div v-if="selectedOrder.orderItems && selectedOrder.orderItems.length">
          <h4>购买菜品：</h4>
          <ul>
            <li v-for="(item, index) in selectedOrder.orderItems" :key="index">
              {{ item.dishName }} x {{ item.quantity }}
            </li>
          </ul>
        </div>
        <!-- 仅在订单状态为待处理时显示取消订单按钮 -->
        <el-button type="danger" v-if="selectedOrder.status === 0" @click="cancelOrder(selectedOrder)">
          取消订单
        </el-button>
        <!-- 当订单状态为已完成时，显示评论功能 -->
        <div v-if="selectedOrder.status === 2" class="comment-section">
          <h4>订单评论</h4>
          <el-input
              type="textarea"
              v-model="comment"
              placeholder="请输入您的评论"
              rows="3"
          ></el-input>
          <el-button type="primary" @click="submitComment(selectedOrder)">提交评论</el-button>
        </div>
        <el-button type="primary" @click="closeModal">关闭</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { getOrdersByUser, getOrderItems, updateOrderStatus, updateComment } from '../api/order';
import { getMerchant, getDishById } from '../api/merchant';

export default {
  data() {
    return {
      orders: [],           // 用户的所有订单，附加上订单项及商家名称
      message: '',          // 错误或提示信息
      selectedOrder: null,  // 当前选中查看详情的订单
      userId: localStorage.getItem('userId'),
      selectedStatus: "",   // 筛选条件，空字符串表示显示所有状态
      comment: ''           // 用于存储用户输入的评论
    };
  },
  created() {
    if (this.userId) {
      this.fetchOrders();
    } else {
      this.message = '用户未登录';
    }
  },
  computed: {
    // 根据 selectedStatus 过滤订单
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
        // 获取用户订单
        const response = await getOrdersByUser(this.userId);
        let orders = response.data;
        // 按下单日期倒序排列
        orders.sort((a, b) => new Date(b.createTime) - new Date(a.createTime));

        // 为每个订单补充商家名称、订单项和订单总金额
        for (let order of orders) {
          try {
            const merchantRes = await getMerchant(order.merchantId);
            order.merchantName = merchantRes.data.name;
          } catch (e) {
            console.error('获取商家信息失败：', e);
            order.merchantName = order.merchantId;
          }

          try {
            const itemsRes = await getOrderItems(order.id);
            order.orderItems = itemsRes.data;
            // 为每个订单项补充菜品名称
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
    // 显示订单详情，并初始化评论内容（如果已存在）
    showOrderDetails(order) {
      this.selectedOrder = order;
      if (order.status === 2) {
        this.comment = order.comment || '';
      }
    },
    closeModal() {
      this.selectedOrder = null;
      this.comment = '';
    },
    // 将订单状态数字转换为文本
    getStatusText(status) {
      const statusMap = {
        0: "待处理",
        1: "已接单",
        2: "已完成",
        3: "已取消",
      };
      return statusMap[status] || "未知状态";
    },
    // 根据订单状态返回对应的 Element UI 标签类型
    statusTagType(status) {
      const tagTypeMap = {
        0: "warning", // 待处理 - 橙色
        1: "primary", // 已接单 - 蓝色
        2: "success", // 已完成 - 绿色
        3: "danger",  // 已取消 - 红色
      };
      return tagTypeMap[status] || "info";
    },
    // 根据订单状态返回对应的 CSS 类
    statusClass(status) {
      switch (status) {
        case 0:
          return 'status-pending';
        case 1:
          return 'status-accepted';
        case 2:
          return 'status-completed';
        case 3:
          return 'status-cancelled';
        default:
          return '';
      }
    },
    // 格式化日期字符串
    formatDate(time) {
      return new Date(time).toLocaleString();
    },
    // 取消订单（仅限待处理订单）
    async cancelOrder(order) {
      if (confirm('确定取消该订单吗？')) {
        try {
          const response = await updateOrderStatus(order.id, 3); // 3 表示“已取消”
          this.$message.success(response.data);
          // 更新本地订单状态
          order.status = 3;
          if (this.selectedOrder && this.selectedOrder.id === order.id) {
            this.selectedOrder.status = 3;
          }
        } catch (error) {
          console.error(error);
          this.$message.error('取消订单失败，请重试。');
        }
      }
    },
    // 提交订单评论，并给出反馈
    async submitComment(order) {
      if (!this.comment) {
        this.$message.error('评论内容不能为空');
        return;
      }
      try {
        const response = await updateComment(order.id, this.comment);
        this.$message.success(response.data);
        // 更新本地订单的评论
        order.comment = this.comment;
      } catch (error) {
        console.error(error);
        this.$message.error('评论失败，请重试。');
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
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

h2 {
  margin-bottom: 20px;
  color: #333;
}

/* 筛选区域 */
.filter-container {
  margin-bottom: 20px;
  text-align: left;
}
.filter-container label {
  margin-right: 10px;
  font-size: 16px;
  color: #333;
}
.filter-container select {
  padding: 6px 8px;
  font-size: 16px;
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
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

/* 订单状态颜色 */
.status-pending {
  color: blue;
}
.status-accepted {
  color: #bdbd00;
}
.status-completed {
  color: green;
}
.status-cancelled {
  color: red;
}

/* 评论区样式 */
.comment-section {
  margin-top: 20px;
}
.comment-section h4 {
  margin-bottom: 10px;
}
</style>
