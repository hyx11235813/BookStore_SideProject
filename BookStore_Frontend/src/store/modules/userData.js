import {getCurrentUser} from "@/api/user";

export default {
    state: {
        user: {
            id: '',
            name: '',
            avatar: '',
        },

        applyResetEmail: '',
        cartProductQuantity: ''
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
            state.applyResetEmail = email
        },
        setCartProductQuantity(state, number) {
            state.cartProductQuantity = number
        },
    },
    actions: {
        //獲取登入者資料
        async fetchUser({commit, state}) {
            //先檢查state是否存有使用者資料，若無才發送請求獲取登入者資料
            let isEmpty = Object.values(state.user).every(item => item === '');
            if (isEmpty) {
                try {
                    const resp = await getCurrentUser();//後端透過請求頭的token獲取使用者資料
                    if (resp.id === undefined) { //後端伺服器關閉在打開會更換token，如果這時候再用原本的token去獲取使用者會得不到資料，所以才要清除殘留的token
                        localStorage.removeItem('token')
                    }
                    let payload = {
                        id: resp.id,
                        name: resp.name,
                        avatar: resp.avatar
                    };
                    commit('setUserInfo', payload);
                } catch (e) {
                    throw new Error('獲取登入資料失敗');
                }
            }
        },

        logout({commit}) {
            localStorage.removeItem('token')
            localStorage.removeItem('cart')
            commit('setUserInfo', '')
            commit('setCartProductQuantity', 0)

        }
    }
}