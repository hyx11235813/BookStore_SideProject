import Vue from 'vue';
import VueRouter from 'vue-router';
import * as view from '@/views';
import store from '@/store';

Vue.use(VueRouter);

const routes = [
    {
        path: '/admin',
        component: view.AdminMain,
        children: [
            {
                path: '',
                name: 'adminHomePage',
                component: view.AdminHomePage,
                meta: {requiresAuth: true}
            },
            {
                path: 'book',
                name: 'adminBookPage',
                component: view.BookManage,
                meta: {requiresAuth: true}
            },
            {
                path: 'deletedBook',
                name: 'adminDeletedBookPage',
                component: view.DeletedBookManage,
                meta: {requiresAuth: true}
            },
            {
                path: 'user',
                name: 'adminUserPage',
                component: view.UserManage,
                meta: {requiresAuth: true}
            }
        ]
    },
    {
        path: '/',
        component: view.UserMain,
        children: [
            {path: '', name: 'home', component: view.UserHomePage},
            {
                path: '/product/detail/:slug/:isbn',
                component: view.BookDetail
            },
            {
                path: '/search',
                name: 'search',
                component: view.SearchBooks

            }
        ],
        meta: {requiresAuth: false}
    },
    {
        path: '/cart',
        component: view.CartMain,
        children: [
            {path: '', name: 'cart', component: view.Cart}
        ]

    },

    {
        path: '/login',
        component: view.LoginMain,
        children: [
            {
                path: '', name: 'login', component: view.Login,
                meta: {requiresAuth: false}
            }

        ]

    },
    {
        path: '/register',
        component: view.RegisterMain,
        children: [
            {path: '', name: 'register', component: view.Register},
            {path: '/register/successInfo', name: 'successInfo', component: view.SuccessInfo}
        ]
    },
    {
        path: '/sent-email-info',
        component: view.SentEmailInfo
    },
    {
        path: '/forgot-password',
        component: view.ResetPasswordMain,
        children: [
            {path: '', name: 'forgotPassword', component: view.ForgetPassword},
            {path: '/forgot-password/verify-token', name: 'verifyToken', component: view.VerifyToken},
            {path: '/forgot-password/reset-password', name: 'resetPassword', component: view.ResetPassword},
            {
                path: '/forgot-password/reset-password-success',
                name: 'resetPasswordSuccess',
                component: view.ResetPasswordSuccess
            }
        ]
    },
    {
        path: '/member-center',
        component: view.MemberCenterMain,
        name: 'memberCenter',
        redirect: '/member-center/personal-info',
        children: [
            {
                path: '/member-center/personal-info',
                name: 'PersonalInfo.',
                component: view.PersonalInfo,
                meta: {requiresAuth: true}
            },
            {
                path: '/member-center/borrowRecord',
                name: 'BorrowRecord',
                component: view.BorrowRecord,
                meta: {requiresAuth: true}
            }
        ]
    },
    {
        path:'/checkout',
        name: 'checkoutMain',
        component:view.Checkout,
        meta: {requiresAuth: true}
    },
    {
        path: '/change-password',
        name: 'changePassword',
        component: view.ChangePassword
    },
    {
        path: '/verify-password',
        name: 'verifyPassword',
        component: view.VerifyPassword
    },
    {
        path: '/error',
        component: view.ErrorPage,
        name: 'error'
    },
    {
        path: '*', redirect: '/'
    }
];

const router = new VueRouter({
    mode: 'history',
    routes
});

// const PUBLIC_ROUTES = ['/', '/login', '/register', '/sent-email-info', '/product', '/forgot-password'];    //不需要檢查是否登入的路由

function isLoggedIn() {
    const token = localStorage.getItem('token');
    if (!token) return false;
    return !isTokenExpired(token);
}

function isTokenExpired(token) {
    try {
        const payload = JSON.parse(atob(token.split('.')[1])); // 解碼 payload
        return payload.exp * 1000 < Date.now(); // true為過期
    } catch (e) {
        console.warn(e);
        return true; // 解析失敗視為過期
    }
}


router.beforeEach(async (to, from, next) => {
    const token = localStorage.getItem('token');
    const loggedIn = token && !isTokenExpired(token);
    if (to.path === '/login' && loggedIn) {
        return next('/');
    }
    if (loggedIn) {
        try {
            await store.dispatch('fetchUser');
        } catch (e) {
            console.warn(e);
            return next('/error');
        }
    }
    // 如果未登入且目標頁面需要授權
    if (!isLoggedIn && to.meta.requiresAuth) {
        console.warn('未登入，權限不足');
        return next('/login');
    }
    next();
});

export default router;