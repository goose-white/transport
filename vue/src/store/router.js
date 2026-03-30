import Vue from 'vue'
import Router from 'vue-router'
import Login from '@/views/login/Login'
import Manage from '@/views/Manage'

// 安装 Router 插件
Vue.use(Router)

// 创建路由实例
const router = new Router({
    routes: [
        {
            path: '/login',
            name: 'Login',
            component: Login
        },
        {
            path: '/',
            redirect: '/home' // 默认跳转到首页
        },
        {
            path: '/home',
            name: 'Manage',
            component: Manage,
            meta: { requiresAuth: true } // 需要登录的路由标记
        }
    ]
})

// 路由守卫：验证登录状态
router.beforeEach((to, from, next) => {
    // 判断目标路由是否需要登录
    if (to.matched.some(record => record.meta.requiresAuth)) {
        // 从本地存储获取 token
        const token = localStorage.getItem('token')
        if (!token) {
            // 无 token 跳转到登录页
            next({ path: '/login' })
        } else {
            // 有 token 正常放行
            next()
        }
    } else {
        // 无需登录的路由直接放行
        next()
    }
})

// 导出路由实例
export default router