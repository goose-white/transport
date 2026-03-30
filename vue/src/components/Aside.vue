<template>
  <div class="aside-container">
    <!-- 垂直导航菜单 -->
    <el-menu
        mode="vertical"
        background-color="#33393B"
        text-color="#E2E8F0"
        active-text-color="#38BDF8"
        :collapse="isCollapse"
        router
        default-active="/home"
    >
      <!-- 主页 -->
      <el-menu-item index="/home">
        <i class="el-icon-house"></i>
        <span slot="title">主页</span>
      </el-menu-item>

      <!-- 系统管理二级菜单 -->
      <el-submenu index="2">
        <template slot="title">
          <i class="el-icon-menu"></i>
          <span>系统管理</span>
        </template>
        <el-menu-item index="/drone">
          <i class="el-icon-s-custom"></i>
          <span slot="title">无人机管理</span>
        </el-menu-item>
        <el-menu-item index="/task">
          <i class="el-icon-s-custom"></i>
          <span slot="title">无人机任务管理</span>
        </el-menu-item>
      </el-submenu>

      <!-- 记录管理二级菜单（修复index重复） -->
      <el-submenu index="3">
        <template slot="title">
          <i class="el-icon-menu"></i>
          <span>记录管理</span>
        </template>
        <el-menu-item index="/photography">
          <i class="el-icon-s-tools"></i>
          <span slot="title">现场视频</span>
        </el-menu-item>
        <el-menu-item index="/recognition">
          <i class="el-icon-s-tools"></i>
          <span slot="title">识别结果</span>
        </el-menu-item>
      </el-submenu>
    </el-menu>

    <!-- 当前页面标题显示区域 -->
    <div
        class="current-title-container"
        v-if="currentTitle"
        :style="{
        padding: isCollapse ? '12px 10px' : '12px 20px',
        borderTop: '1px solid #444',
        textAlign: isCollapse ? 'center' : 'left'
      }"
    >
      <span style="font-size: 13px; color: #90CDF4;">当前页面：</span>
      <span style="font-size: 14px; color: #E2E8F0; font-weight: 500;">{{ currentTitle }}</span>
    </div>
  </div>
</template>

<script>
export default {
  name: "Aside",
  props: {
    isCollapse: {
      type: Boolean,
      required: true,
      default: false
    },
    logoTextShow: {
      type: Boolean,
      required: false,
      default: true
    },
    currentTitle: {
      type: String,
      required: false,
      default: ''
    }
  }
}
</script>

<style scoped>
.aside-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

/* 消除Element UI默认边框 */
:deep(.el-menu) {
  border-right: none;
  height: 100%;
}

/* 标题容器样式 */
.current-title-container {
  margin-top: auto;
}
</style>