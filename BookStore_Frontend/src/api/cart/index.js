import request from "@/utils/request";

/**
 * 將購物車資料儲到資料庫
 */
export const addToShoppingCart = (cart) => {
    return request.post('/user/cart', cart)
}

/**
 * 獲取用戶的購物車資料
 */
export const getUserCartInfo = (userId) => {
    return request.get(`/user/cart/${userId}`)
}
/**
 * 更改、刪除用戶購物車商品數量
 */


export const updateCartQuantity = (payload) => {
    return request.post('/user/cart/update-quantity', payload)
}