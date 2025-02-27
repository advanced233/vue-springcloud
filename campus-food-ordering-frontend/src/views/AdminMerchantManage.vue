<template>
  <div class="container">
    <div class="page-wrapper">
      <h2>商户管理</h2>

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
              <el-descriptions-item label="电话">
                {{ merchant.phone }}
              </el-descriptions-item>
              <el-descriptions-item label="状态">
                <el-tag :type="statusTagType(merchant.status)">
                  {{ getStatusText(merchant.status) }}
                </el-tag>
              </el-descriptions-item>
            </el-descriptions>

            <!-- 操作按钮：如果商户状态不是0（已封禁），则显示“封禁”按钮，否则显示“解禁”按钮 -->
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

export default {
  name: 'AdminMerchantManage',
  data() {
    return {
      merchants: [], // 所有商户信息
      message: ''    // 提示信息
    };
  },
  created() {
    this.fetchMerchants();
  },
  methods: {
    // 获取所有商户信息
    async fetchMerchants() {
      try {
        const response = await getAllMerchants();
        // 后端返回的所有商户列表
        this.merchants = response.data || [];
      } catch (error) {
        console.error(error);
        this.message = '获取商户列表失败，请稍后重试。';
      }
    },
    // 将商户状态转换为可读文本
    getStatusText(status) {
      // 约定：0 => 已封禁, 1 => 正常, 其他 => 未知
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
        // 后端可能返回一条提示信息，如“封禁成功”
        this.message = response.data;
        // 更新本地数据
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
        // 更新本地数据
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
