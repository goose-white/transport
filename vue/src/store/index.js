import Vue from 'vue'
import Vuex from 'vuex'

// 安装Vuex插件
Vue.use(Vuex)

// 创建Vuex仓库并导出
export default new Vuex.Store({
    state: {
        currentPathName: ''
    },
    mutations: {
        setPath(state) {
            state.currentPathName = localStorage.getItem("currentPathName")
        }
    }
})