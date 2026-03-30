// src/utils/request.js
import axios from 'axios'

// 创建axios实例
const request = axios.create({
    baseURL: 'http://localhost:9091', // 后端服务地址（必须和你的后端端口一致）
    timeout: 5000, // 请求超时时间
    headers: {
        'Content-Type': 'application/json;charset=utf-8'
    }
})

// 请求拦截器（可选）
request.interceptors.request.use(
    config => {
        return config
    },
    error => {
        console.error('请求错误：', error)
        return Promise.reject(error)
    }
)

// 响应拦截器（可选）
request.interceptors.response.use(
    response => {
        return response
    },
    error => {
        console.error('响应错误：', error)
        return Promise.reject(error)
    }
)

export default request