import request from '@/utils/request';

/**
 * 獲取登入的使用者資料
 */
export const getCurrentUser = () => {
    return request.get('/user-info/user');
};

/**
 * 登入
 *
 * @param {Object} data 表單
 * @param {string} data.loginIdentifier 帳號或信箱
 * @param {string} data.password 密碼
 */
export const login = (data) => {
    return request.post('/user/login', data);
};

/**
 * 檢查email是否已被註冊或是否符合email格式
 */
export const checkEmail = (email) => {
    return request.get(`/register/check-email/${encodeURIComponent(email)}`);
};

/**
 * 檢查帳號是否已被註冊
 */
export const checkAccount = (account) => {
    return request.get(`/register/check-account/${encodeURIComponent(account)}`);
};

/**
 * 註冊會員
 *
 * @param {Object} data 表單
 * @param {string} data.account 帳號
 * @param {string} data.name 用戶名稱
 * @param {string} data.password 密碼
 * @param {string} data.email  電子郵件
 */
export const register = (data) => {
    return request.post('/register/', data);
};

/**
 * 申請重設密碼，發送驗證碼到用戶的電子郵件
 *
 * @param {Object} data 表單
 * @param {string} data.email 電子郵件
 */
export const requestResetPassword = (data) => {
    return request.post('/user/forgot-password/request', data);
};

/**
 * 驗證重設密碼的token
 *
 * @param data  表單
 * @param {string} data.email  電子郵件
 * @param {string} data.token  驗證碼
 */
export const verifyToken = (data) => {
    return request.post('/user/forgot-password/verify-token', data);
};

/**
 * 重設密碼
 *
 * @param data 表單
 * @param {string} data.email  電子郵件
 * @param {string} data.token  驗證碼（驗證通過的那組）
 * @param {string} data.password  新密碼
 */
export const resetPassword = (data) => {
    return request.post('/user/forgot-password/reset-password', data);
};

//user-info開頭/////////////////////////////////////////////////////

/**
 * 修改密碼
 *
 * @param data  表單
 * @param {string} data.password  新密碼
 */
export const changePassword = (data) => {
    return request.put('/user-info/change-password', data);
};

/**
 * 上傳用戶頭像
 *
 * @param data
 * @param {string} data.avatar - 用戶頭像路徑（存在本地上的路徑）
 */
export const uploadAvatar = (data) => {
    return request.put('/user-info/upload-avatar', data);
};

/**
 * 儲存圖片到本地上
 *
 * @param {FormData} formData 包含 file 欄位
 */
export const uploadImage = (formData) => {
    return request.post('/file/upload', formData, {
        headers: {'Content-Type': undefined} //讓瀏覽器自動帶上 multipart 的 boundary
    });
};

/**
 * 刪除用戶頭像
 *
 * @param {String} id  用戶id
 */
export const removeAvatar = (id) => {
    return request.put(`/user-info/remove-avatar/${id}`);
};

/**
 * 更改用戶名稱
 *
 * @param data 表單
 * @param data.name  用戶名稱
 */
export const updateUserName = (data) => {
    return request.put('/user-info/change-name', data);
};
