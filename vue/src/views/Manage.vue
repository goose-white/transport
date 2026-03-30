<template>
  <el-container style="min-height: 100vh;">
    <!-- 顶部标题栏 -->
    <el-header
        style="
        background-color: #2D2D2D;
        padding: 0 20px;
        height: auto;
        min-height: 70px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        border-bottom: 1px solid #444;
      "
    >
      <!-- 左侧标题 -->
      <div style="display: block; text-align: left;">
        <div style="font-size: 22px; font-weight: 800; color: #4299E1;">无人机管理系统</div>
        <div style="font-size: 16px; font-weight: 500; margin-top: 4px; color: #90CDF4;">
          Drone Management System
        </div>
      </div>

      <!-- 右侧区域 -->
      <div style="display: flex; align-items: center; gap: 20px; color: #E2E8F0;">
        <!-- 通知框 -->
        <el-dropdown placement="bottom-end">
          <span class="el-dropdown-link" style="position: relative; cursor: pointer;">
            <i class="el-icon-bell" style="font-size: 18px;"></i>
            <span
                style="
                position: absolute;
                top: -5px;
                right: -8px;
                background: #F56C6C;
                color: #fff;
                font-size: 12px;
                width: 18px;
                height: 18px;
                line-height: 18px;
                text-align: center;
                border-radius: 50%;
              "
            >
              3
            </span>
          </span>
          <el-dropdown-menu slot="dropdown" style="width: 300px; padding: 10px;">
            <div
                style="
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 10px;
              "
            >
              <span style="font-weight: bold;">今日待办</span>
              <el-button type="text" size="mini" style="color: #4299E1;">更多</el-button>
            </div>
            <el-dropdown-item style="padding: 10px 0; border-bottom: 1px solid #eee;">
              <div style="display: flex; align-items: center; gap: 10px;">
                <span
                    style="
                    background: #4299E1;
                    color: #fff;
                    font-size: 12px;
                    padding: 2px 6px;
                    border-radius: 4px;
                  "
                >
                  待办
                </span>
                <div>
                  <div style="font-size: 14px;">检查无人机设备状态</div>
                  <div style="font-size: 12px; color: #999;" id="todo-date"></div>
                </div>
              </div>
            </el-dropdown-item>
            <el-dropdown-item style="padding: 10px 0;">
              <div style="display: flex; align-items: center; gap: 10px;">
                <span
                    style="
                    background: #4299E1;
                    color: #fff;
                    font-size: 12px;
                    padding: 2px 6px;
                    border-radius: 4px;
                  "
                >
                  待办
                </span>
                <div>
                  <div style="font-size: 14px;">完成无人机任务上报</div>
                  <div style="font-size: 12px; color: #999;" id="todo-date2"></div>
                </div>
              </div>
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>

        <!-- 搜索框 -->
        <el-input
            placeholder="请输入搜索内容"
            style="width: 200px; background: rgba(255,255,255,0.1); border: none; color: #E2E8F0;"
            prefix-icon="el-icon-search"
        ></el-input>

        <!-- 日期 -->
        <span style="font-size: 14px;" id="current-date"></span>

        <!-- 退出按钮 -->
        <el-button
            type="text"
            icon="el-icon-switch-button"
            style="color: #E2E8F0; font-size: 16px;"
            @click="logout"
        >
          退出登录
        </el-button>

        <!-- 替换为：登录时的名称+ID（从本地存储读取） -->
        <div style="display: flex; align-items: center; gap: 8px; cursor: pointer;">
          <el-avatar
              icon="el-icon-user"
              size="medium"
              style="background: #4299E1; color: #fff;"
          ></el-avatar>
          <div style="text-align: left;">
            <span style="font-size: 14px; display: block;">{{ loginUsername || '未登录' }}</span>

          </div>
        </div>
      </div>
    </el-header>

    <!-- 椭圆形边框+底色的标签页区域 -->
    <div style="background: #f8f9fa; padding: 8px 20px; border-bottom: 1px solid #e9ecef;">
      <el-tabs
          v-model="activeTab"
          style="height: 36px; line-height: 36px;"
          @tab-click="handleTabClick"
          @tab-remove="closeTab"
      >
        <!-- 自定义椭圆形标签样式（修复标点错误） -->
        <template slot="tab" slot-scope="{ tab, index }">
          <div
              :style="{
              display: 'inline-flex',
              alignItems: 'center',
              padding: '0 18px',
              height: '36px',
              marginRight: '8px',
              borderRadius: '18px',
              cursor: 'pointer',
              background: tab.active ? '#4299E1' : '#e9ecef',
              color: tab.active ? '#fff' : '#6c757d',
              fontSize: '14px',
              fontWeight: tab.active ? 500 : 400,
              border: 'none',
              transition: 'all 0.2s ease'
            }"
          >
            {{ tab.label }}
            <<i
              v-if="tab.closable"
              class="el-icon-close"
              style="margin-left: 8px; font-size: 12px; cursor: pointer;"
              :style="{ color: tab.active ? 'rgba(255,255,255,0.8)' : '#6c757d' }"
              @mouseenter="(e) => e.target.style.color = tab.active ? '#fff' : '#F56C6C'"
              @mouseleave="(e) => e.target.style.color = tab.active ? 'rgba(255,255,255,0.8)' : '#6c757d'"
              @click.stop="closeTab(tab.name)"
          ></i>
          </div>
        </template>

        <!-- 标签列表 -->
        <el-tab-pane
            v-for="tab in tabs"
            :key="tab.path"
            :label="tab.name"
            :name="tab.path"
            :closable="tab.path !== '/home'"
        ></el-tab-pane>
      </el-tabs>
    </div>

    <!-- 主体区域 -->
    <el-container style="flex: 1;">
      <el-aside
          :width="sideWidth + 'px'"
          style="background-color: #33393B; border-right: 1px solid #ccc;"
      >
        <Aside :is-collapse="isCollapse" :logoTextShow="logoTextShow" />
      </el-aside>
      <el-container>
        <el-main style="padding: 20px; background: #fff; min-height: calc(100vh - 158px);">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </el-container>
</template>

<script>
import Aside from "@/components/Aside";

export default {
  components: { Aside },
  data() {
    return {
      isCollapse: false,
      sideWidth: 300,
      logoTextShow: true,
      timeTimer: null,
      activeTab: "/home", // 当前激活标签
      tabs: [
        // 初始标签
        { path: "/home", name: "主页" },
      ],
      // 新增：存储登录信息（从本地存储读取）
      loginUsername: "",
      loginUserId: ""
    };
  },
  methods: {
    // 折叠侧边栏
    collapse() {
      this.isCollapse = !this.isCollapse;
      this.sideWidth = this.isCollapse ? 64 : 300;
      this.logoTextShow = !this.isCollapse;
    },
    // 退出登录
    logout() {
      // 退出时清空本地存储+登录信息
      localStorage.clear();
      this.loginUsername = "";
      this.loginUserId = "";
      this.$router.push("/login");
    },
    // 更新日期时间
    getCurrentDate() {
      const date = new Date();
      const year = date.getFullYear();
      const month = (date.getMonth() + 1).toString().padStart(2, "0");
      const day = date.getDate().toString().padStart(2, "0");
      const hour = date.getHours().toString().padStart(2, "0");
      const minute = date.getMinutes().toString().padStart(2, "0");
      const second = date.getSeconds().toString().padStart(2, "0");
      const week = ["周日", "周一", "周二", "周三", "周四", "周五", "周六"][
          date.getDay()
          ];
      const dateText = `${year}-${month}-${day} ${week} ${hour}:${minute}:${second}`;

      // 防错处理
      const currentDateEl = document.getElementById("current-date");
      const todoDateEl = document.getElementById("todo-date");
      const todoDate2El = document.getElementById("todo-date2");

      if (currentDateEl) currentDateEl.innerText = dateText;
      if (todoDateEl) todoDateEl.innerText = dateText;
      if (todoDate2El) todoDate2El.innerText = dateText;
    },
    // 添加标签页
    addTab(route) {
      const isExist = this.tabs.some((tab) => tab.path === route.path);
      if (!isExist) {
        // 标签名称映射
        const tabNameMap = {
          "/home": "主页",
          "/drone": "无人机管理",
          "/task": "无人机任务管理",
          "/photography": "现场视频",
          "/recognition": "识别结果",
        };
        this.tabs.push({
          path: route.path,
          name: tabNameMap[route.path] || "未知页面",
        });
      }
      this.activeTab = route.path;
    },
    // 点击标签切换路由
    handleTabClick(tab) {
      this.$router.push(tab.name);
      this.activeTab = tab.name;
    },
    // 关闭标签页
    closeTab(path) {
      const tabIndex = this.tabs.findIndex((tab) => tab.path === path);
      if (tabIndex > -1) {
        this.tabs.splice(tabIndex, 1);

        // 关闭当前标签时自动切换
        if (this.activeTab === path) {
          this.activeTab = this.tabs.length > 0 ? this.tabs[this.tabs.length - 1].path : "/home";
          this.$router.push(this.activeTab);
        }
      }
    },
    // 新增：从本地存储读取登录信息
    getLoginInfo() {
      this.loginUsername = localStorage.getItem("username") || "";
      this.loginUserId = localStorage.getItem("userId") || "";
    }
  },
  watch: {
    // 路由变化自动添加标签
    $route: {
      immediate: true,
      handler(to) {
        this.addTab(to);
        // 路由切换时重新读取登录信息
        this.getLoginInfo();
      },
    },
  },
  mounted() {
    // 初始化时间
    this.getCurrentDate();
    this.timeTimer = setInterval(() => {
      this.getCurrentDate();
    }, 1000);

    // 初始化：读取登录信息
    this.getLoginInfo();
  },
  beforeDestroy() {
    // 清除定时器
    if (this.timeTimer) {
      clearInterval(this.timeTimer);
    }
  },
};
</script>

<style scoped>
/* 覆盖Element UI默认样式 */
:deep(.el-tabs) {
  --el-tabs-header-height: 36px;
}
:deep(.el-tabs__header) {
  margin: 0 !important;
  border-bottom: none !important;
}
:deep(.el-tabs__content) {
  display: none !important; /* 隐藏默认内容，用router-view展示 */
}
/* 标签hover效果优化 */
:deep(.el-tabs__item:hover) {
  opacity: 0.9;
}
</style>