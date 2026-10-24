/**
 * localStorage 的存取集中在這裡，避免各元件各自 JSON.parse / 寫死 key
 */
const TOKEN_KEY = 'token';
const CART_KEY = 'cart';
const REMEMBER_LOGIN_KEY = 'remember-login';

const readJson = (key, fallback) => {
    try {
        const raw = localStorage.getItem(key);
        return raw ? JSON.parse(raw) : fallback;
    } catch (e) {
        console.warn(`讀取 localStorage(${key}) 失敗`, e);
        return fallback;
    }
};

export const tokenStorage = {
    get: () => readJson(TOKEN_KEY, ''),
    set: (token) => localStorage.setItem(TOKEN_KEY, JSON.stringify(token)),
    clear: () => localStorage.removeItem(TOKEN_KEY),

    /** token 是否存在且尚未過期 */
    isValid() {
        const token = this.get();
        if (!token) return false;
        try {
            const payload = JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')));
            return payload.exp * 1000 > Date.now();
        } catch (e) {
            console.warn(e);
            return false;
        }
    }
};

/**
 * 未登入時的購物車：{ [bookId]: quantity }
 */
export const guestCart = {
    get: () => readJson(CART_KEY, {}),
    set: (cart) => localStorage.setItem(CART_KEY, JSON.stringify(cart)),
    clear: () => localStorage.removeItem(CART_KEY),
    /** 購物車中的商品種類數量 */
    count() {
        return Object.keys(this.get()).length;
    },
    /** 加入商品，數量最多增加到 maxQuantity（庫存上限，不傳代表不限制），回傳更新後的購物車 */
    add(bookId, quantity = 1, maxQuantity = Infinity) {
        const cart = this.get();
        const current = cart[bookId] || 0;
        const next = Math.min(current + quantity, maxQuantity);
        if (next > current) {
            cart[bookId] = next;
            this.set(cart);
        }
        return cart;
    }
};

export const rememberedLogin = {
    get: () => localStorage.getItem(REMEMBER_LOGIN_KEY),
    set: (identifier) => localStorage.setItem(REMEMBER_LOGIN_KEY, identifier),
    clear: () => localStorage.removeItem(REMEMBER_LOGIN_KEY)
};
