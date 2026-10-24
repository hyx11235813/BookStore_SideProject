const { defineConfig } = require('@vue/cli-service');

module.exports = defineConfig({
  transpileDependencies: true,
  lintOnSave: 'warning',
  devServer: {
    port: 8080,
    proxy: {
      // 前端開發時，把 /api 開頭的請求轉發給後端（後端網址可用 VUE_APP_API_PROXY_TARGET 覆寫）
      '/api': {
        target: process.env.VUE_APP_API_PROXY_TARGET || 'http://localhost:8081'
      }
    }
  }
});
