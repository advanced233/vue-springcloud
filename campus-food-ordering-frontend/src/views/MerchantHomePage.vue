<template>
  <div class="container">
    <!-- 页面卡片容器 -->
    <el-card class="page-wrapper" shadow="hover">
      <!-- 标题及管理按钮 -->
      <template #header>
        <div class="header-wrapper">
          <el-button type="text" icon="el-icon-back" @click="goBack">返回</el-button>
          <h2>商家菜品管理</h2>
          <div class="management-buttons">
            <el-button
                type="primary"
                icon="el-icon-circle-plus"
                @click="openAddModal"
            >
              添加新菜品
            </el-button>
            <el-button
                :type="deleteMode ? 'info' : 'danger'"
                icon="el-icon-delete"
                @click="toggleDeleteMode"
            >
              {{ deleteMode ? '退出删除模式' : '删除菜品' }}
            </el-button>
            <el-button type="warning" icon="el-icon-tickets" @click="goToOrderManage">
              订单管理
            </el-button>
            <el-button type="primary" @click="goToPersonalInfo" icon="el-icon-user">
              个人信息
            </el-button>
          </div>
        </div>
      </template>

      <!-- 菜品卡片列表 -->
      <el-row :gutter="20" justify="center" class="dishes-wrapper">
        <el-col
            v-for="dish in dishes"
            :key="dish.id"
            :xs="24"
            :sm="12"
            :md="8"
            :lg="6"
        >
          <el-card shadow="hover" class="dish-item">
            <div class="dish-image">
              <el-image
                  v-if="dish.image"
                  :src="dish.image"
                  fit="contain"
                  class="dish-image-el"
              />
              <span v-else>No Image</span>
            </div>
            <div class="dish-info">
              <h3>{{ dish.name }}</h3>
              <p>{{ dish.description }}</p>
              <p class="price">价格: {{ dish.price }} 元</p>
              <p class="status">状态: {{ dish.status === 1 ? '上架' : '下架' }}</p>
            </div>
            <el-divider></el-divider>
            <div class="card-actions">
              <el-button
                  size="mini"
                  type="primary"
                  @click="toggleStatus(dish)"
              >
                {{ dish.status === 1 ? '下架' : '上架' }}
              </el-button>
              <el-button
                  size="mini"
                  type="warning"
                  @click="openEditModal(dish)"
              >
                编辑
              </el-button>
              <el-button
                  v-if="deleteMode"
                  size="mini"
                  type="danger"
                  @click="onDeleteDish(dish)"
              >
                删除
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 全局提示信息（错误提示） -->
      <el-alert
          v-if="message"
          :title="message"
          type="error"
          show-icon
          class="alert-message"
      />
    </el-card>

    <!-- 添加菜品弹窗 -->
    <el-dialog
        v-model="showAddModal"
        title="添加新菜品"
        width="400px"
    >
      <el-form :model="currentDish" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="currentDish.name" placeholder="请输入菜品名称"></el-input>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="currentDish.description" placeholder="请输入描述"></el-input>
        </el-form-item>
        <el-form-item label="价格">
          <el-input
              v-model.number="currentDish.price"
              placeholder="请输入价格"
              type="number"
          ></el-input>
        </el-form-item>
        <el-form-item label="图片">
          <el-input v-model="currentDish.image" placeholder="请输入图片链接"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="closeAddModal">取消</el-button>
        <el-button type="primary" @click="saveNewDish">保存</el-button>
      </template>
    </el-dialog>

    <!-- 编辑菜品弹窗 -->
    <el-dialog
        v-model="showEditModal"
        title="编辑菜品"
        width="400px"
    >
      <el-form :model="currentDish" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="currentDish.name" placeholder="请输入菜品名称"></el-input>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="currentDish.description" placeholder="请输入描述"></el-input>
        </el-form-item>
        <el-form-item label="价格">
          <el-input
              v-model.number="currentDish.price"
              placeholder="请输入价格"
              type="number"
          ></el-input>
        </el-form-item>
        <el-form-item label="图片">
          <el-input v-model="currentDish.image" placeholder="请输入图片链接"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="closeEditModal">取消</el-button>
        <el-button type="primary" @click="saveEditDish">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getDishesByMerchantId,
  changeDishStatus,
  updateDish,
  addDish,
  deleteDish
} from '../api/merchant'

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
    }
  },
  created() {
    // 从 localStorage 中获取商家ID（登录时已存入）
    this.merchantId = localStorage.getItem('merchantId')
    if (!this.merchantId) {
      this.message = '未登录或无效的商家ID'
      return
    }
    this.fetchDishes()
  },
  methods: {
    // 获取当前商家的菜品列表
    async fetchDishes() {
      try {
        const response = await getDishesByMerchantId(this.merchantId)
        this.dishes = response.data
      } catch (error) {
        console.error(error)
        this.message = '获取菜品列表失败，请重试。'
        ElMessage.error('获取菜品列表失败')
      }
    },
    goBack() {
      this.$router.go(-1);
    },
    // 切换菜品状态（上架/下架）
    async toggleStatus(dish) {
      const newStatus = dish.status === 1 ? 0 : 1
      try {
        const response = await changeDishStatus(dish.id, newStatus)
        if (response.data.includes('成功')) {
          dish.status = newStatus
          this.message = `菜品已${newStatus === 1 ? '上架' : '下架'}`
          ElMessage.success(this.message)
        } else {
          this.message = response.data
          ElMessage.error(this.message)
        }
      } catch (error) {
        console.error(error)
        this.message = '操作失败，请重试。'
        ElMessage.error(this.message)
      }
    },
    // 打开编辑弹窗，并将当前菜品信息复制到 currentDish
    openEditModal(dish) {
      this.currentDish = { ...dish }
      this.showEditModal = true
    },
    // 保存编辑后的菜品信息
    async saveEditDish() {
      try {
        const response = await updateDish(this.currentDish)
        if (response.data.includes('成功')) {
          // 更新本地菜品列表
          const index = this.dishes.findIndex(d => d.id === this.currentDish.id)
          if (index !== -1) {
            this.dishes[index] = { ...this.currentDish }
          }
          this.message = '菜品更新成功'
          ElMessage.success(this.message)
          this.showEditModal = false
        } else {
          this.message = response.data
          ElMessage.error(this.message)
        }
      } catch (error) {
        console.error(error)
        this.message = '更新失败，请重试。'
        ElMessage.error(this.message)
      }
    },
    closeEditModal() {
      this.showEditModal = false
    },
    // 打开添加菜品弹窗，重置 currentDish
    openAddModal() {
      this.currentDish = {
        name: '',
        description: '',
        price: 0,
        image: '',
        status: 1
      }
      this.showAddModal = true
    },
    // 保存新添加的菜品
    async saveNewDish() {
      try {
        // 将商家ID附加到菜品对象上
        const newDish = { ...this.currentDish, merchantId: this.merchantId }
        const response = await addDish(newDish)
        if (response.data.includes('成功')) {
          this.message = '菜品添加成功'
          ElMessage.success(this.message)
          this.showAddModal = false
          // 重新获取菜品列表（或直接将返回的新菜品添加到 dishes 数组中）
          this.fetchDishes()
        } else {
          this.message = response.data
          ElMessage.error(this.message)
        }
      } catch (error) {
        console.error(error)
        this.message = '添加失败，请重试。'
        ElMessage.error(this.message)
      }
    },
    closeAddModal() {
      this.showAddModal = false
    },
    // 切换删除模式
    toggleDeleteMode() {
      this.deleteMode = !this.deleteMode
    },
    // 删除菜品
    async onDeleteDish(dish) {
      try {
        await ElMessageBox.confirm(
            `确定删除菜品「${dish.name}」吗？`,
            '提示',
            {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
            }
        )
        // 如果用户点击“确定”，则调用接口删除
        const response = await deleteDish(dish.id)
        if (response.data.includes('成功')) {
          this.message = '菜品删除成功'
          ElMessage.success(this.message)
          // 从本地列表中移除该菜品
          this.dishes = this.dishes.filter(d => d.id !== dish.id)
        } else {
          this.message = response.data
          ElMessage.error(this.message)
        }
      } catch (error) {
        // 用户点了“取消”或者出现异常
        if (error !== 'cancel') {
          console.error(error)
          this.message = '删除失败，请重试。'
          ElMessage.error(this.message)
        }
      }
    },
    // 跳转到订单管理页面
    goToOrderManage() {
      this.$router.push('/merchant/ordermanage')
    },
    goToPersonalInfo() {
      this.$router.push({ path: '/merchant/personalinfo' });
    }
  }
}
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

/* 标题与操作按钮 */
.header-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.management-buttons > * {
  margin-right: 10px;
}

/* 菜品列表区域 */
.dishes-wrapper {
  margin-top: 20px;
}

/* 菜品卡片 */
.dish-item {
  cursor: default;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  margin-bottom: 20px;
}

.dish-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

/* 菜品图片 */
.dish-image {
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

.dish-image-el {
  width: 100%;
  height: 100%;
}

/* 菜品信息 */
.dish-info {
  margin-top: 10px;
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
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
}

/* 全局提示信息 */
.alert-message {
  margin-top: 20px;
}
</style>
