# BookStore

前後端分離的網路書店：`BookStore_Backend`（Spring Boot 3 / Java 17 / SQL Server）＋ `BookStore_Frontend`（Vue 2）。

## 本機啟動

1. **後端設定**：複製 `BookStore_Backend/src/main/resources/application-local.properties.example`
   為 `application-local.properties`，填入資料庫密碼、`JWT_SECRET`（至少 32 字元）與郵件帳密。
   這個檔案已被 `.gitignore` 忽略，機敏資訊不要寫進版本控制。
2. 執行 `start_project.bat`（會同時啟動前端 `:8080` 與後端 `:8081`）。

後端也可以不用 local 檔案，改用環境變數：`DB_PASSWORD`、`JWT_SECRET`、`MAIL_USERNAME`、`MAIL_PASSWORD`
（完整清單見 `application.properties`）。

## 測試

```
cd BookStore_Backend && mvnw.cmd test        # 後端單元／Web 層測試（不需要資料庫）
cd BookStore_Frontend && npm run lint        # 前端
```

## API 約定

- 路徑都在 `/api` 底下，回應格式統一為 `{code, data, message}`，`code` 與 HTTP status 一致。
- 需要登入的 API 以 `token` 標頭帶 JWT；`/api/admin/**` 只有管理員（role = `ADMIN`）可以使用。
