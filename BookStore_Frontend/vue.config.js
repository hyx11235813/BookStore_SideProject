const { defineConfig } = require("@vue/cli-service");
module.exports = defineConfig({
  transpileDependencies: true,
  lintOnSave: false,
  // publicPath: '/static/',//npm run build 配置的資源引入路徑前綴
  devServer: {
    port: 8080,
    proxy: {
      '/api': {
        target: 'http://localhost:8081', // 後端Port
        // pathRewrite: {
        //   // '^/api': '/admin' // 重寫發送路徑
        // }
      }
    }
  }
})