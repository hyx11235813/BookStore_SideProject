import request from '@/utils/request';

/**
 * 將購物車資料儲到資料庫（使用者由 token 判斷）
 * @param {Array<{bookId: number|string, quantity: number}>} items
 */
export const addToShoppingCart = (items) => {
    return request.post('/user/cart', items);
};

/**
 * 獲取用戶的購物車資料
 */
export const getUserCartInfo = (userId) => {
    return request.get(`/user/cart/${userId}`);
};

/**
 * 更改、刪除用戶購物車商品數量
 * @param {{bookId: number, modifyType: 'ADD'|'SUBTRACT'}} payload
 */
export const updateCartQuantity = (payload) => {
    return request.post('/user/cart/update-quantity', payload);
};
