import {getCurrentUser} from '@/api/user';
import {guestCart, tokenStorage} from '@/utils/storage';

const emptyUser = () => ({
    id: '',
    name: '',
    avatar: ''
});

export default {
    state: {
        user: emptyUser(),
        applyResetEmail: '', //申請重設密碼的信箱
        applyResetToken: '', //已通過驗證、可用來重設密碼的驗證碼
        cartProductQuantity: 0
    },
    mutations: {
        setUserName(state, userName) {
            state.user.name = userName;
        },
        setUserAvatar(state, avatar) {
            state.user.avatar = avatar;
        },
        setUserInfo(state, user) {
            state.user = user;
        },
        setApplyResetEmail(state, email) {
            state.applyResetEmail = email;
        },
        setApplyResetToken(state, token) {
            state.applyResetToken = token;
        },
        setCartProductQuantity(state, number) {
            state.cartProductQuantity = number;
        }
    },
    actions: {
        //獲取登入者資料
        async fetchUser({commit, state}) {
            if (state.user.id) return; //已經有使用者資料就不用再請求
            try {
                const resp = await getCurrentUser(); //後端透過請求頭的token獲取使用者資料
                if (resp.code !== 200) {
                    tokenStorage.clear(); //token 失效（例如後端更換了金鑰），清除殘留的token
                    return;
                }
                const {id, name, avatar} = resp.data;
                commit('setUserInfo', {id, name, avatar});
            } catch (e) {
                throw new Error('獲取登入資料失敗');
            }
        },

        logout({commit}) {
            tokenStorage.clear();
            guestCart.clear();
            commit('setUserInfo', emptyUser());
            commit('setCartProductQuantity', 0);
        }
    }
};
