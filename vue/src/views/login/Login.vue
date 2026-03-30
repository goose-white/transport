<template>
  <div class="login-container">
    <!-- 动态背景（交通主题：无人机+道路+车辆+行人） -->
    <div class="login-bg">
      <!-- 道路基底 -->
      <div class="bg-road"></div>
      <!-- 道路标线 -->
      <div class="road-line"></div>
      <!-- 动态车辆（CSS绘制） -->
      <div class="car car-1"></div>
      <div class="car car-2"></div>
      <!-- 动态行人（CSS绘制） -->
      <div class="pedestrian ped-1"></div>
      <div class="pedestrian ped-2"></div>
      <!-- 无人机剪影 -->
      <div class="bg-drone"></div>
    </div>

    <!-- 登录卡片 -->
    <el-card class="login-card" shadow="hover">
      <div class="card-header">
        <div class="logo">
          <!-- 无人机图标+交通标识组合 -->
          <div class="drone-icon">
            <div class="drone-body"></div>
            <div class="drone-wing left"></div>
            <div class="drone-wing right"></div>
            <div class="drone-propeller left"></div>
            <div class="drone-propeller right"></div>
          </div>
          <span>交通无人机管理系统</span>
        </div>
        <div class="desc">交通巡检 · 无人机调度 · 智能管控</div>
      </div>

      <!-- 注册成功提示弹窗 -->
      <transition name="success-fade">
        <div class="success-tip" v-if="showSuccessTip">
          <i class="el-icon-circle-check"></i>
          <div class="tip-text">
            <p class="title">注册成功！</p>
            <p class="sub-title">即将为您跳转到登录页面...</p>
          </div>
        </div>
      </transition>

      <el-tabs v-model="activeTab" type="border-card" class="tab-container" :disabled="showSuccessTip">
        <!-- 登录标签页 -->
        <el-tab-pane label="登录" name="login">
          <el-form :model="loginForm" label-width="0" class="login-form">
            <el-form-item prop="username">
              <el-input
                  v-model="loginForm.username"
                  placeholder="请输入账号"
                  prefix-icon="el-icon-user"
                  class="input-item"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                  v-model="loginForm.password"
                  type="password"
                  placeholder="请输入密码"
                  prefix-icon="el-icon-lock"
                  class="input-item"
              />
            </el-form-item>
            <el-form-item class="form-actions">
              <el-button
                  type="primary"
                  @click="handleLogin"
                  class="login-btn"
                  icon="el-icon-right"
              >
                登录
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 注册标签页 -->
        <el-tab-pane label="注册" name="register">
          <el-form :model="registerForm" label-width="0" class="login-form">
            <el-form-item prop="username">
              <el-input
                  v-model="registerForm.username"
                  placeholder="请输入账号"
                  prefix-icon="el-icon-user"
                  class="input-item"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                  v-model="registerForm.password"
                  type="password"
                  placeholder="请输入密码（至少6位）"
                  prefix-icon="el-icon-lock"
                  class="input-item"
              />
            </el-form-item>
            <el-form-item prop="confirmPassword">
              <el-input
                  v-model="registerForm.confirmPassword"
                  type="password"
                  placeholder="请确认密码"
                  prefix-icon="el-icon-lock"
                  class="input-item"
              />
            </el-form-item>
            <el-form-item class="form-actions">
              <el-button
                  type="primary"
                  @click="handleRegister"
                  class="login-btn"
                  icon="el-icon-check"
              >
                注册
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: "Login",
  data() {
    return {
      activeTab: "login",
      loginForm: { username: "", password: "" },
      registerForm: { username: "", password: "", confirmPassword: "" },
      showSuccessTip: false // 注册成功提示开关
    };
  },
  methods: {
    // 登录（取消密码加密，新增存储用户名和工号）
    async handleLogin() {
      // 基础校验
      if (!this.loginForm.username || !this.loginForm.password) {
        this.$message.error("请输入账号和密码！");
        return;
      }

      try {
        // 直接传明文密码（取消加密）
        const res = await request.post('/user/login', {
          username: this.loginForm.username.trim(),
          password: this.loginForm.password.trim()
        });

        console.log("登录接口返回：", res.data); // 调试用

        if (res.data.code === '200') {
          // 存储token、用户名（注册账号）、工号（ID）
          localStorage.setItem("token", res.data.data.token);
          localStorage.setItem("username", res.data.data.username); // 登录人员姓名（注册账号）
          localStorage.setItem("userId", res.data.data.id); // 工号（对应用户ID）

          this.$message.success(res.data.msg || "登录成功！");
          // 跳转到首页
          this.$router.push("/home");
        } else {
          this.$message.error(res.data.msg || "登录失败");
        }
      } catch (error) {
        console.error("登录请求失败：", error);
        this.$message.error("登录失败，请检查网络或接口！");
      }
    },

    // 注册（取消密码加密）
    async handleRegister() {
      // 基础校验
      if (!this.registerForm.username || !this.registerForm.password) {
        this.$message.error("请输入账号和密码！");
        return;
      }
      if (this.registerForm.password.length < 6) {
        this.$message.error("密码长度不能少于6位！");
        return;
      }
      if (this.registerForm.password !== this.registerForm.confirmPassword) {
        this.$message.error("两次密码不一致！");
        return;
      }

      try {
        // 直接传明文密码（取消加密）
        const res = await request.post('/user/register', {
          username: this.registerForm.username.trim(),
          password: this.registerForm.password.trim()
        });

        console.log("注册接口返回：", res.data); // 调试用

        if (res.data.code === '200') {
          // 显示注册成功提示
          this.showSuccessTip = true;
          this.$message({
            type: 'success',
            message: res.data.msg || '注册成功！',
            duration: 2000
          });

          // 3秒后自动切换到登录页，并清空表单
          setTimeout(() => {
            this.activeTab = "login";
            this.registerForm = { username: "", password: "", confirmPassword: "" };
            this.showSuccessTip = false;
          }, 3000);
        } else {
          this.$message.error(res.data.msg || "注册失败");
        }
      } catch (error) {
        console.error("注册请求失败：", error);
        this.$message.error("注册失败，请检查网络或接口！");
      }
    }
  }
};
</script>

<style scoped>
/* 登录页面容器 */
.login-container {
  height: 100vh;
  width: 100vw;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f0f2f5;
  overflow: hidden;
  position: relative;
}

/* 动态背景（交通主题） */
.login-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #2b6cb0 0%, #1a365d 100%);
  animation: bg-shift 15s ease infinite;
  background-size: 400% 400%;
  z-index: 0;
}

/* 背景渐变动画 */
@keyframes bg-shift {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

/* 道路基底 */
.bg-road {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 180px;
  background: rgba(0, 0, 0, 0.2);
  border-top: 2px solid rgba(255, 255, 255, 0.3);
}

/* 道路标线 */
.road-line {
  position: absolute;
  bottom: 90px;
  left: 0;
  width: 100%;
  height: 4px;
  background: repeating-linear-gradient(
      to right,
      rgba(255, 255, 255, 0.4),
      rgba(255, 255, 255, 0.4) 60px,
      transparent 60px,
      transparent 120px
  );
  animation: road-line 15s linear infinite;
}
@keyframes road-line {
  0% { transform: translateX(0); }
  100% { transform: translateX(-120px); }
}

/* 车辆元素（CSS绘制） */
.car {
  position: absolute;
  bottom: 60px;
  width: 40px;
  height: 20px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 4px;
  animation: car-drive linear infinite;
}
/* 车辆1：轿车 */
.car-1 {
  left: -50px;
  animation-duration: 20s;
}
.car-1::before { /* 车头 */
  content: '';
  position: absolute;
  top: 2px;
  right: -8px;
  width: 8px;
  height: 16px;
  background: rgba(255, 255, 255, 0.8);
  border-top-right-radius: 4px;
  border-bottom-right-radius: 4px;
}
.car-1::after { /* 车轮 */
  content: '';
  position: absolute;
  width: 8px;
  height: 8px;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  bottom: -4px;
  left: 6px;
  box-shadow: 24px 0 0 rgba(0, 0, 0, 0.5);
}
/* 车辆2：SUV（更大） */
.car-2 {
  left: -50px;
  bottom: 90px;
  width: 50px;
  height: 25px;
  animation-duration: 25s;
  animation-delay: 5s;
}
.car-2::before {
  content: '';
  position: absolute;
  top: 3px;
  right: -10px;
  width: 10px;
  height: 19px;
  background: rgba(255, 255, 255, 0.8);
  border-top-right-radius: 5px;
  border-bottom-right-radius: 5px;
}
.car-2::after {
  content: '';
  position: absolute;
  width: 10px;
  height: 10px;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  bottom: -5px;
  left: 8px;
  box-shadow: 30px 0 0 rgba(0, 0, 0, 0.5);
}
/* 车辆行驶动画 */
@keyframes car-drive {
  0% { transform: translateX(0); }
  100% { transform: translateX(calc(100vw + 100px)); }
}

/* 行人元素（CSS绘制） */
.pedestrian {
  position: absolute;
  bottom: 20px;
  width: 12px;
  height: 20px;
  animation: walk linear infinite;
}
/* 行人1 */
.ped-1 {
  left: -30px;
  animation-duration: 30s;
}
.ped-1::before { /* 头部 */
  content: '';
  position: absolute;
  top: 0;
  left: 2px;
  width: 8px;
  height: 8px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 50%;
}
.ped-1::after { /* 身体+腿 */
  content: '';
  position: absolute;
  top: 8px;
  left: 3px;
  width: 6px;
  height: 12px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 2px;
  box-shadow:
      0 8px 0 -2px rgba(255, 255, 255, 0.8), /* 左腿 */
      3px 8px 0 -2px rgba(255, 255, 255, 0.8); /* 右腿 */
}
/* 行人2 */
.ped-2 {
  left: -30px;
  bottom: 20px;
  animation-duration: 35s;
  animation-delay: 10s;
}
.ped-2::before {
  content: '';
  position: absolute;
  top: 0;
  left: 2px;
  width: 8px;
  height: 8px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 50%;
}
.ped-2::after {
  content: '';
  position: absolute;
  top: 8px;
  left: 3px;
  width: 6px;
  height: 12px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 2px;
  box-shadow:
      3px 8px 0 -2px rgba(255, 255, 255, 0.8), /* 左腿 */
      0 8px 0 -2px rgba(255, 255, 255, 0.8); /* 右腿 */
}
/* 行人走路动画 */
@keyframes walk {
  0% { transform: translateX(0); }
  100% { transform: translateX(calc(100vw + 50px)); }
}

/* 无人机剪影 */
.bg-drone {
  position: absolute;
  top: 20%;
  right: 15%;
  width: 80px;
  height: 50px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  transform: rotate(15deg);
  animation: drone-fly 25s linear infinite;
}
.bg-drone::before, .bg-drone::after {
  content: '';
  position: absolute;
  width: 30px;
  height: 10px;
  background: rgba(255, 255, 255, 0.1);
  top: 20px;
}
.bg-drone::before { left: -30px; }
.bg-drone::after { right: -30px; }
@keyframes drone-fly {
  0% { right: -10%; top: 20%; }
  50% { top: 30%; }
  100% { right: 110%; top: 20%; }
}

/* 登录卡片 */
.login-card {
  width: 420px;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  background-color: rgba(255, 255, 255, 0.95);
  z-index: 1;
  overflow: hidden;
  transition: transform 0.3s ease;
}
.login-card:hover {
  transform: translateY(-5px);
}

/* 注册成功提示样式 */
.success-tip {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 255, 255, 0.98);
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  z-index: 10;
  border-radius: 12px;
}
.success-tip i {
  font-size: 60px;
  color: #67c23a;
  margin-bottom: 16px;
  animation: success-bounce 0.8s ease;
}
.success-tip .title {
  font-size: 24px;
  color: #333;
  font-weight: bold;
  margin-bottom: 8px;
}
.success-tip .sub-title {
  font-size: 14px;
  color: #666;
}
/* 成功提示动画 */
@keyframes success-bounce {
  0% { transform: scale(0); }
  60% { transform: scale(1.2); }
  100% { transform: scale(1); }
}
/* 渐变过渡效果 */
.success-fade-enter-active, .success-fade-leave-active {
  transition: all 0.5s ease;
}
.success-fade-enter, .success-fade-leave-to {
  opacity: 0;
  transform: scale(0.9);
}

/* 系统标识：无人机图标 */
.card-header {
  padding: 24px;
  text-align: center;
}
.logo {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: bold;
  color: #2b6cb0;
  margin-bottom: 8px;
}
.drone-icon {
  position: relative;
  width: 36px;
  height: 36px;
  margin-right: 12px;
}
.drone-body {
  position: absolute;
  top: 12px;
  left: 8px;
  width: 20px;
  height: 12px;
  background: #4299e1;
  border-radius: 6px;
}
.drone-wing {
  position: absolute;
  top: 16px;
  width: 20px;
  height: 4px;
  background: #2b6cb0;
  border-radius: 2px;
}
.drone-wing.left { left: -12px; }
.drone-wing.right { right: -12px; }
.drone-propeller {
  position: absolute;
  top: 8px;
  width: 8px;
  height: 8px;
  border: 2px solid #2b6cb0;
  border-radius: 50%;
  animation: propeller-spin 1s linear infinite;
}
.drone-propeller.left { left: -16px; }
.drone-propeller.right { right: -16px; }
@keyframes propeller-spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.desc {
  font-size: 14px;
  color: #666;
}

/* 表单/按钮样式 */
.tab-container {
  padding: 0 24px 24px;
}
.login-form {
  margin-top: 16px;
}
.input-item {
  height: 48px;
  font-size: 14px;
  border-radius: 8px;
  border-color: #e5e7eb;
  transition: all 0.3s ease;
}
.input-item:focus {
  border-color: #4299e1;
  box-shadow: 0 0 0 2px rgba(66, 153, 225, 0.2);
}
.form-actions {
  margin-top: 24px;
}
.login-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  border-radius: 8px;
  background: linear-gradient(135deg, #4299e1 0%, #2b6cb0 100%);
  border: none;
  transition: all 0.3s ease;
}
.login-btn:hover {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(66, 153, 225, 0.3);
}
</style>