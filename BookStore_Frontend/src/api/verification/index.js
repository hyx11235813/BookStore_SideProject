import request from "@/utils/request"


/**
 * 驗證輸入的密碼跟目前登入的用戶密碼是否一致
 *
 * @param data 表單
 * @param {String} data.password - 密碼
 */
export const matchPassword = (data) => {
    return request.post('/verification/password', data)
}