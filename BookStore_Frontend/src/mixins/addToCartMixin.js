import {addToShoppingCart, getUserCartInfo} from '@/api/cart';
import {guestCart} from '@/utils/storage';

const HTTP_OK = 200;

/**
 * 「加入購物車」按鈕共用的邏輯：
 * 登入的使用者存到資料庫，未登入的存到 localStorage，並同步更新 header 顯示的購物車數量。
 */
export default {
    methods: {
        /**
         * @param {number|string} bookId 書籍 id
         * @param {Object} [options]
         * @param {number} [options.quantity=1] 加入的數量
         * @param {number} [options.maxQuantity] 未登入時的數量上限（庫存）
         * @returns {Promise<boolean>} 是否加入成功
         */
        async addBookToCart(bookId, {quantity = 1, maxQuantity = Infinity} = {}) {
            const userId = this.$store.state.userData.user.id;
            if (!userId) {
                const cart = guestCart.add(bookId, quantity, maxQuantity);
                this.$store.commit('setCartProductQuantity', Object.keys(cart).length);
                return true;
            }

            try {
                const res = await addToShoppingCart([{bookId, quantity}]);
                if (res.code !== HTTP_OK) return false;
                await this.refreshUserCartQuantity(userId);
                return true;
            } catch (error) {
                console.warn(error);
                return false;
            }
        },
        async refreshUserCartQuantity(userId) {
            try {
                const quantity = await getUserCartInfo(userId).then(res => res.data?.length || 0);
                this.$store.commit('setCartProductQuantity', quantity);
            } catch (error) {
                console.warn(error);
            }
        }
    }
};
