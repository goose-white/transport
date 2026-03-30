import store from '@/store'
import Vue from 'vue'
import VueRouter from 'vue-router'

// 移除绝对路径（无需使用，webpack会自动解析相对路径）
// import HomeView from 'C:/Users/38390/Desktop/drone/vue/vue/src/views/Home.vue'

Vue.use(VueRouter)

const routes = [
  // 登录页路由（新增！必须添加）
  {
    path: '/login',
    name: 'Login',
    // 确保Login组件路径正确（根据你的实际文件路径调整）
    component: () => import('../views/login/Login.vue')
  },
  // 主路由（需要登录）
  {
    path: '/',
    component: () => import(/* webpackChunkName: "about" */ '../views/Manage.vue'),
    redirect: "/home",
    // 给主路由添加登录验证标记（子路由会继承）
    meta: { requiresAuth: true },
    children: [
      {
        path: 'home',
        name: "无人机管理中心",
        component: () => import(/* webpackChunkName: "about" */ '../views/Home.vue')
      },
      {
        path: 'drone',
        name: "无人机",
        component: () => import(/* webpackChunkName: "about" */ '../views/drone.vue')
      },
      {
        path: 'task',
        name: "无人机任务",
        component: () => import(/* webpackChunkName: "about" */ '../views/task.vue')
      },
      {
        path: 'Photography',
        name: "现场视频",
        component: () => import(/* webpackChunkName: "about" */ '../views/Photography.vue')
      },
      {
        path: 'recognition',
        name: "识别记录",
        component: () => import(/* webpackChunkName: "about" */ '../views/recognition.vue')
      },
    ]
  },
  {
    path: '/about',
    name: 'about',
    component: () => import(/* webpackChunkName: "about" */ '../views/AboutView.vue'),
    // 可选：about页无需登录，可不加meta
    meta: { requiresAuth: false }
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

// 防止重复点击路由报错
const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err)
}

// 路由守卫：验证登录状态
router.beforeEach((to, from, next) => {
  console.log('目标路由：', to.path, '需要登录：', to.matched.some(r => r.meta.requiresAuth));
  if (to.matched.some(record => record.meta.requiresAuth)) {
    const token = localStorage.getItem('token');
    console.log('当前token：', token);
    if (!token) {
      next('/login'); // 无token跳登录页
    } else {
      next(); // 有token正常放行
    }
  } else {
    next(); // 无需登录的路由直接放行
  }
});

export default router