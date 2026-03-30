const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    hot: false, // 关闭热更新（合法属性）
    liveReload: false, // 关闭页面自动刷新（合法属性）
    // 仅保留watchFiles合法格式，不额外加任何非法属性
    watchFiles: ['non-existent-file.txt'] // 合法非空数组，路径不存在=无文件监听
  }
})