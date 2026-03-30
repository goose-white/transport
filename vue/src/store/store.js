import Vue from 'vue'
import Vuex from 'vuex'

// 安装 Vuex 插件
Vue.use(Vuex)

// 创建并导出 Vuex 仓库
const store = new Vuex.Store({
    state: {
        currentPathName: ''
    },
    mutations: {
        setPath(state) {
            state.currentPathName = localStorage.getItem("currentPathName")
        }
    }
})

export default store