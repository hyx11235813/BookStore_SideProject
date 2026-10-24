# BookStore Frontend

Vue 2 + Vuex + Vue Router + Element UI。

## 啟動

```
npm install
npm run serve     # 開發模式，http://localhost:8080
npm run build     # 正式環境打包
npm run lint      # 檢查程式碼風格
```

開發時 `/api` 的請求會由 `vue.config.js` 代理到後端（預設 `http://localhost:8081`），
可用環境變數 `VUE_APP_API_PROXY_TARGET` 改成其他網址。

## 專案結構

```
src/
├── api/          各功能的後端 API 呼叫（只負責組請求，不處理畫面）
├── assets/       圖片、字型、共用的 SCSS 變數
├── components/   可重複使用的元件
├── mixins/       跨元件共用的邏輯（購物車、訊息提示）
├── router/       路由表（routes.js）與登入驗證（index.js）
├── store/        Vuex：登入使用者、購物車數量、側邊欄狀態
├── utils/        request（axios 設定）、storage（token / 訪客購物車）、format
└── views/        頁面
```

## 與後端的約定

- 所有回應格式為 `{code, data, message}`，`code` 與 HTTP status 一致。
  `utils/request.js` 會把業務錯誤（例如 401、404）也當成一般回應回傳，
  所以呼叫端只要檢查 `resp.code`；只有網路中斷等情況才會丟出例外。
- 登入後 token 存在 localStorage，透過 `token` 請求標頭帶給後端（見 `utils/storage.js`）。
- 未登入的購物車存在 localStorage，登入後會自動合併到資料庫。
