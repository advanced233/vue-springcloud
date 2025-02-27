<template>
  <div class="container">
    <div class="page-wrapper">
      <h2>个人信息</h2>
      <el-form :model="form" label-width="100px">
        <!-- 头像 -->
        <el-form-item label="头像">
          <div class="avatar-container">
            <el-input v-model="form.avatar" placeholder="请输入头像URL" />
            <img v-if="form.avatar" :src="form.avatar" alt="头像" class="avatar-preview" />
          </div>
        </el-form-item>

        <!-- 用户ID（只读） -->
        <el-form-item label="用户ID">
          <el-input v-model="form.id" disabled />
        </el-form-item>

        <!-- 昵称 -->
        <el-form-item label="昵称">
          <el-input v-model="form.username" />
        </el-form-item>

        <!-- 密码（只显示空，若用户想修改可输入新密码） -->
        <el-form-item label="密码">
          <el-input
              type="password"
              v-model="form.password"
              placeholder="若不修改，请留空"
          />
        </el-form-item>

        <!-- 手机号 -->
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>

        <!-- 地址列表 -->
        <el-form-item label="地址">
          <div v-for="(addr, index) in form.addresses" :key="index" class="address-item">
            <el-input
                v-model="addr.address"
                placeholder="请输入地址"
                style="width: 40%; margin-right: 10px;"
            />
            <el-input
                v-model="addr.tag"
                placeholder="标签"
                style="width: 20%; margin-right: 10px;"
            />
            <el-checkbox
                v-model="addr.isDefault"
                :true-label="1"
                :false-label="0"
                style="margin-right: 10px;"
            >
              默认地址
            </el-checkbox>
            <!-- 改用小按钮，带文字“删除” -->
            <el-button
                type="danger"
                icon="el-icon-delete"
                size="small"
                @click="removeAddress(index)"
            >
              删除
            </el-button>
          </div>
          <el-button
              type="primary"
              icon="el-icon-plus"
              @click="addAddress"
              style="margin-top: 10px;"
          >
            添加地址
          </el-button>
        </el-form-item>

        <!-- 保存按钮 -->
        <el-form-item>
          <el-button type="primary" @click="savePersonalInfo">保存</el-button>
        </el-form-item>
      </el-form>
      <p v-if="message" class="message">{{ message }}</p>
    </div>
  </div>
</template>

<script>
import { getUserPersonalInfo, updateUserPersonalInfo } from '../api/user';

export default {
  name: 'UserPersonalInformation',
  data() {
    return {
      form: {
        id: null,
        username: '',
        password: '',
        phone: '',
        avatar: '',
        addresses: []
      },
      message: ''
    };
  },
  created() {
    const userId = localStorage.getItem('userId');
    if (userId) {
      this.fetchPersonalInfo(userId);
    } else {
      this.message = '用户未登录';
    }
  },
  methods: {
    // 获取用户个人信息
    fetchPersonalInfo(userId) {
      getUserPersonalInfo(userId)
          .then(response => {
            let data = response.data || {};
            // 不显示加密后的密码
            data.password = '';
            if (!data.addresses) {
              data.addresses = [];
            }
            this.form = data;
          })
          .catch(error => {
            console.error(error);
            this.message = '获取个人信息失败';
          });
    },
    // 保存更新后的用户个人信息
    savePersonalInfo() {
      updateUserPersonalInfo(this.form)
          .then(response => {
            this.message = response.data;
          })
          .catch(error => {
            console.error(error);
            this.message = '更新个人信息失败';
          });
    },
    // 添加新地址
    addAddress() {
      this.form.addresses.push({
        address: '',
        tag: '',
        isDefault: 0
      });
    },
    // 移除地址
    removeAddress(index) {
      this.form.addresses.splice(index, 1);
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

/* 头像区域 */
.avatar-container {
  display: flex;
  align-items: center;
}

.avatar-preview {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  margin-left: 10px;
}

/* 地址项样式 */
.address-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

/* 提示信息 */
.message {
  margin-top: 15px;
  font-size: 16px;
  color: #ff4d4f;
}
</style>
