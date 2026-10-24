import Vue from 'vue';
import VueRouter from 'vue-router';
import store from '@/store';
import {tokenStorage} from '@/utils/storage';
import routes from './routes';

Vue.use(VueRouter);

const router = new VueRouter({
    mode: 'history',
    routes
});

/**
 * vue-router 3 的 push / replace 會回傳 Promise，被守衛重新導向（例如未登入被導去登入頁）、
 * 重複導向同一頁時 Promise 會 reject。這些是預期內的情況，不需要當成錯誤，其他錯誤仍照常拋出。
 */
const IGNORED_FAILURES = [
    VueRouter.NavigationFailureType.redirected,
    VueRouter.NavigationFailureType.duplicated,
    VueRouter.NavigationFailureType.cancelled
];

['push', 'replace'].forEach((method) => {
    const original = router[method];
    router[method] = function (location, onComplete, onAbort) {
        if (onComplete || onAbort) {
            return original.call(this, location, onComplete, onAbort);
        }
        return original.call(this, location).catch((error) => {
            if (VueRouter.isNavigationFailure(error) && IGNORED_FAILURES.some(type => VueRouter.isNavigationFailure(error, type))) {
                return error;
            }
            throw error;
        });
    };
});

router.beforeEach(async (to, from, next) => {
    const loggedIn = tokenStorage.isValid();

    if (loggedIn) {
        if (to.path === '/login') {
            return next('/');
        }
        try {
            await store.dispatch('fetchUser');
        } catch (e) {
            console.warn(e);
            return next('/error');
        }
    } else if (tokenStorage.get()) {
        tokenStorage.clear(); //清掉已過期的 token
    }

    const requiresAuth = to.matched.some(route => route.meta.requiresAuth);
    // 目標頁面需要登入，但目前沒有有效的 token（包含 fetchUser 發現 token 失效的情況）
    if (requiresAuth && !tokenStorage.isValid()) {
        console.warn('未登入，權限不足');
        return next('/login');
    }
    next();
});

export default router;
