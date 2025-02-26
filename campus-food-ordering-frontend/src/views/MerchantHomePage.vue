<template>
  <div class="container">
    <div class="page-wrapper">
      <h2>商家菜品管理</h2>

      <!-- 页面级管理按钮 -->
      <div class="management-buttons">
        <button @click="openAddModal">添加新菜品</button>
        <button @click="toggleDeleteMode">
          {{ deleteMode ? '退出删除模式' : '删除菜品' }}
        </button>
        <!-- 新增订单管理按钮 -->
        <button @click="goToOrderManage">订单管理</button>
      </div>

      <!-- 菜品卡片区域 -->
      <div class="dishes-wrapper">
        <div class="dish-card" v-for="dish in dishes" :key="dish.id">
          <div class="dish-image">
            <img v-if="dish.image" :src="dish.image" alt="Dish Image" />
            <div v-else class="placeholder">No Image</div>
          </div>
          <div class="dish-info">
            <h3>{{ dish.name }}</h3>
            <p>{{ dish.description }}</p>
            <p class="price">价格: {{ dish.price }} 元</p>
            <p class="status">状态: {{ dish.status === 1 ? '上架' : '下架' }}</p>
            <!-- 卡片操作按钮 -->
            <div class="card-actions">
              <button @click="toggleStatus(dish)">
                {{ dish.status === 1 ? '下架' : '上架' }}
              </button>
              <button @click="openEditModal(dish)">编辑</button>
              <!-- 删除按钮仅在删除模式下显示 -->
              <button v-if="deleteMode" @click="deleteDish(dish)">删除</button>
            </div>
          </div>
        </div>
      </div>

      <!-- 提示消息 -->
      <p v-if="message" class="message">{{ message }}</p>
    </div>

    <!-- 添加菜品弹窗 -->
    <div class="modal" v-if="showAddModal">
      <div class="modal-content">
        <div class="modal-header">添加新菜品</div>
        <div class="modal-body">
          <div class="form-group">
            <label>菜品名称:</label>
            <input v-model="currentDish.name" type="text" placeholder="请输入菜品名称" />
          </div>
          <div class="form-group">
            <label>描述:</label>
            <input v-model="currentDish.description" type="text" placeholder="请输入描述" />
          </div>
          <div class="form-group">
            <label>价格:</label>
            <input v-model.number="currentDish.price" type="number" placeholder="请输入价格" />
          </div>
          <div class="form-group">
            <label>图片URL:</label>
            <input v-model="currentDish.image" type="url" placeholder="请输入图片链接" />
          </div>
        </div>
        <div class="modal-footer">
          <button @click="saveNewDish">保存</button>
          <button @click="closeAddModal">取消</button>
        </div>
      </div>
    </div>

    <!-- 编辑菜品弹窗 -->
    <div class="modal" v-if="showEditModal">
      <div class="modal-content">
        <div class="modal-header">编辑菜品</div>
        <div class="modal-body">
          <div class="form-group">
            <label>菜品名称:</label>
            <input v-model="currentDish.name" type="text" placeholder="请输入菜品名称" />
          </div>
          <div class="form-group">
            <label>描述:</label>
            <input v-model="currentDish.description" type="text" placeholder="请输入描述" />
          </div>
          <div class="form-group">
            <label>价格:</label>
            <input v-model.number="currentDish.price" type="number" placeholder="请输入价格" />
          </div>
          <div class="form-group">
            <label>图片URL:</label>
            <input v-model="currentDish.image" type="url" placeholder="请输入图片链接" />
          </div>
        </div>
        <div class="modal-footer">
          <button @click="saveEditDish">保存</button>
          <button @click="closeEditModal">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {
  getDishesByMerchantId,
  changeDishStatus,
  updateDish,
  addDish,
  deleteDish
} from '../api/merchant';

export default {
  data() {
    return {
      dishes: [],         // 商家所有菜品
      message: '',        // 提示信息
      deleteMode: false,  // 删除模式标识
      showAddModal: false, // 控制添加菜品弹窗显示
      showEditModal: false, // 控制编辑菜品弹窗显示
      currentDish: {
        // 用于添加或编辑菜品的临时对象
        name: '',
        description: '',
        price: 0,
        image: '',
        status: 1  // 默认添加菜品时设置为上架状态
      },
      merchantId: null    // 商家ID，从登录时存入 localStorage
    };
  },
  created() {
    // 从 localStorage 中获取商家ID（登录时已存入）
    this.merchantId = localStorage.getItem('merchantId');
    if (!this.merchantId) {
      this.message = '未登录或无效的商家ID';
      return;
    }
    this.fetchDishes();
  },
  methods: {
    // 获取当前商家的菜品列表
    async fetchDishes() {
      try {
        console.log(this.merchantId)
        const response = await getDishesByMerchantId(this.merchantId);
        this.dishes = response.data;
      } catch (error) {
        console.error(error);
        this.message = '获取菜品列表失败，请重试。';
      }
    },
    // 切换菜品状态（上架/下架）
    async toggleStatus(dish) {
      const newStatus = dish.status === 1 ? 0 : 1;
      try {
        const response = await changeDishStatus(dish.id, newStatus);
        if (response.data.includes('成功')) {
          dish.status = newStatus;
          this.message = `菜品已${newStatus === 1 ? '上架' : '下架'}`;
        } else {
          this.message = response.data;
        }
      } catch (error) {
        console.error(error);
        this.message = '操作失败，请重试。';
      }
    },
    // 打开编辑弹窗，并将当前菜品信息复制到 currentDish
    openEditModal(dish) {
      this.currentDish = { ...dish };
      this.showEditModal = true;
    },
    // 保存编辑后的菜品信息
    async saveEditDish() {
      try {
        console.log("订单请求数据:", JSON.stringify(this.currentDish, null, 2));
        const response = await updateDish(this.currentDish);
        console.log("订单请求数据:", JSON.stringify(this.currentDish, null, 2));
        if (response.data.includes('成功')) {
          // 更新本地菜品列表
          const index = this.dishes.findIndex(d => d.id === this.currentDish.id);
          if (index !== -1) {
            this.dishes[index] = { ...this.currentDish };
          }
          this.message = '菜品更新成功';
          this.showEditModal = false;
        } else {
          this.message = response.data;
        }
      } catch (error) {
        console.error(error);
        this.message = '更新失败，请重试。';
      }
    },
    closeEditModal() {
      this.showEditModal = false;
    },
    // 打开添加菜品弹窗，重置 currentDish
    openAddModal() {
      this.currentDish = {
        name: '',
        description: '',
        price: 0,
        image: '',
        status: 1
      };
      this.showAddModal = true;
    },
    // 保存新添加的菜品
    async saveNewDish() {
      try {
        // 将商家ID附加到菜品对象上
        const newDish = { ...this.currentDish, merchantId: this.merchantId };
        const response = await addDish(newDish);
        if (response.data.includes('成功')) {
          this.message = '菜品添加成功';
          this.showAddModal = false;
          // 重新获取菜品列表（或直接将返回的新菜品添加到 dishes 数组中）
          this.fetchDishes();
        } else {
          this.message = response.data;
        }
      } catch (error) {
        console.error(error);
        this.message = '添加失败，请重试。';
      }
    },
    closeAddModal() {
      this.showAddModal = false;
    },
    // 切换删除模式，进入或退出“删除模式”
    toggleDeleteMode() {
      this.deleteMode = !this.deleteMode;
    },
    // 删除菜品，调用后端删除接口
    async deleteDish(dish) {
      if (confirm(`确定删除菜品 ${dish.name} 吗？`)) {
        try {
          const response = await deleteDish(dish.id);
          if (response.data.includes('成功')) {
            this.message = '菜品删除成功';
            // 从本地列表中移除该菜品
            this.dishes = this.dishes.filter(d => d.id !== dish.id);
          } else {
            this.message = response.data;
          }
        } catch (error) {
          console.error(error);
          this.message = '删除失败，请重试。';
        }
      }
    },
    // 新增的跳转到订单管理页面的方法
    goToOrderManage() {
      this.$router.push('/merchant/ordermanage');
    }
  }
};
</script>

<style scoped>
/* 整体页面样式 */
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
  text-align: center;
}

/* 页面级管理按钮 */
.management-buttons {
  margin-bottom: 20px;
}

.management-buttons button {
  margin: 0 10px;
  padding: 8px 16px;
  font-size: 16px;
  color: #fff;
  background-color: #007bff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.management-buttons button:hover {
  background-color: #0056b3;
}

/* 菜品卡片区域 */
.dishes-wrapper {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  justify-content: center;
}

/* 单个菜品卡片 */
.dish-card {
  width: 220px;
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
  background-color: #fff;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.dish-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

/* 菜品图片 */
.dish-image {
  width: 100%;
  height: 120px;
  background-color: #f0f0f0;
  display: flex;
  justify-content: center;
  align-items: center;
}

.dish-image img {
  max-width: 100%;
  max-height: 100%;
}

.placeholder {
  color: #aaa;
}

/* 菜品信息 */
.dish-info {
  padding: 12px;
  text-align: left;
}

.dish-info h3 {
  margin: 0 0 8px;
  color: #333;
  font-size: 16px;
}

.dish-info p {
  margin: 0;
  color: #777;
  font-size: 14px;
}

.price {
  margin-top: 8px;
  color: #333;
  font-weight: bold;
}

.status {
  margin-top: 4px;
  font-size: 14px;
}

/* 卡片操作按钮 */
.card-actions {
  margin-top: 10px;
  display: flex;
  justify-content: space-between;
}

.card-actions button {
  flex: 1;
  margin: 0 2px;
  padding: 4px 6px;
  font-size: 14px;
  color: #fff;
  background-color: #007bff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.card-actions button:hover {
  background-color: #0056b3;
}

/* 提示信息 */
.message {
  margin-top: 15px;
  font-size: 16px;
  color: #ff4d4f;
}

/* 弹窗样式 */
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
}

.modal-header {
  font-size: 20px;
  margin-bottom: 10px;
}

.modal-body .form-group {
  margin-bottom: 10px;
  text-align: left;
}

.modal-body label {
  display: block;
  margin-bottom: 5px;
  color: #555;
}

.modal-body input {
  width: 100%;
  padding: 6px 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.modal-footer {
  margin-top: 10px;
  text-align: right;
}

.modal-footer button {
  margin-left: 10px;
  padding: 6px 12px;
  font-size: 14px;
  color: #fff;
  background-color: #007bff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.modal-footer button:hover {
  background-color: #0056b3;
}
</style>
