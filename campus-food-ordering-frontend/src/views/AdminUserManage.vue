<template>
  <div class="container">
    <div class="page-wrapper">
      <h2>用户管理</h2>

      <!-- 用户列表 -->
      <ul class="user-list">
        <li
            v-for="user in users"
            :key="user.id"
            class="user-item"
        >
          <el-card class="user-summary">
            <template #header>
              <div class="user-header">
                <span>用户ID: {{ user.id }}</span>
              </div>
            </template>
            <el-descriptions :column="1" border>
              <el-descriptions-item label="用户名">
                {{ user.username }}
              </el-descriptions-item>
              <el-descriptions-item label="电话">
                {{ user.phone }}
              </el-descriptions-item>
              <el-descriptions-item label="状态">
                <el-tag :type="statusTagType(user.status)">
                  {{ getStatusText(user.status) }}
                </el-tag>
              </el-descriptions-item>
            </el-descriptions>

            <!-- 操作按钮：如果用户状态不是0（已封禁），则显示“封禁”按钮，否则显示“解禁”按钮 -->
            <div class="user-actions">
              <el-button
                  v-if="user.status !== 0"
                  type="danger"
                  size="small"
                  @click="banUser(user)"
              >
                封禁
              </el-button>
              <el-button
                  v-else
                  type="success"
                  size="small"
                  @click="unbanUser(user)"
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
//import { getAllUsers, updateUserStatus } from '../api/user'; // 需要你在 user.js 中定义

export default {
  name: 'AdminUserManage',
  data() {
    return {
      users: [],    // 所有用户信息
      message: ''   // 提示信息
    };
  },
  created() {
    this.fetchUsers();
  },
  methods: {
    // 获取所有用户信息
    async fetchUsers() {
      try {
        const response = await getAllUsers();
        // 后端返回的所有用户列表
        this.users = response.data || [];
      } catch (error) {
        console.error(error);
        this.message = '获取用户列表失败，请稍后重试。';
      }
    },
    // 将用户状态转换为可读文本
    getStatusText(status) {
      // 约定：0 => 已封禁, 1 => 正常, 其他 => 未知
      if (status === 0) return '已封禁';
      if (status === 1) return '正常';
      return '未知状态';
    },
    // 根据用户状态返回 Tag 的颜色
    statusTagType(status) {
      if (status === 0) return 'danger';  // 已封禁 - 红色
      if (status === 1) return 'success'; // 正常 - 绿色
      return 'info';
    },
    // 封禁用户（status=0）
    async banUser(user) {
      if (!confirm(`确定要封禁用户【${user.username}】吗？`)) {
        return;
      }
      try {
        const response = await updateUserStatus(user.id, 0);
        // 后端可能返回一条提示信息，如“封禁成功”
        this.message = response.data;
        // 更新本地数据
        user.status = 0;
      } catch (error) {
        console.error(error);
        this.message = '封禁失败，请稍后重试。';
      }
    },
    // 解禁用户（status=1）
    async unbanUser(user) {
      if (!confirm(`确定要解禁用户【${user.username}】吗？`)) {
        return;
      }
      try {
        const response = await updateUserStatus(user.id, 1);
        this.message = response.data;
        user.status = 1;
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

/* 用户列表 */
.user-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.user-item {
  margin-bottom: 20px;
}

.user-summary {
  padding: 15px;
}

.user-header {
  font-weight: 500;
  font-size: 16px;
}

.user-actions {
  margin-top: 10px;
  text-align: right;
}

.message {
  margin-top: 15px;
  font-size: 16px;
  color: #ff4d4f;
}
</style>
