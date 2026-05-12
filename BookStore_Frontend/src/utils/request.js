import axios from 'axios';
import router from '@/router';
const ERROR_PAGE_PATH = '/error';
const request = axios.create({
    headers: {
        'Content-Type': 'application/json;charset=utf-8'
    },
    baseURL: '/api' //爲了proxy
    // timeout: 10000
});


request.interceptors.request.use(function (config) {
    console.log(config.url);
    config.headers['token'] = JSON.parse(localStorage.getItem('token') || '{}');
    return config;
}, function (error) {
    return Promise.reject(error);
});
request.interceptors.response.use(
    (response) => {
        if (response?.data?.code === 403) {
            router.back()
            console.warn('權限不足');
            return Promise.reject(new Error('403 Forbidden')); // 拒絕請求，防止返回 undefined
        }
        return response?.data;
    },
    (error) => {
        console.log(error, 'error');
        // const msg = error.response?.data?.message || '系統錯誤，請稍候再試';
        // window?.vm?.$root?.showPopupMessage?.(msg, 'warning');
        const status = error.response?.status;
        // if (status === 401) {
        //     console.warn('未授權，請重新登入');
        //     return Promise.reject({ message: '未登入', status });
        // }
        //
        // if (status === 403) {
        //     console.warn('權限不足');
        //     return Promise.reject({ message: '權限不足', status });
        // }
        // if (status === 500){
        //     console.warn('伺服器錯誤');
        //     return Promise.reject({ message: '伺服器錯誤', status });
        // }

        return Promise.reject(error); // 其他錯誤，交由呼叫 API 的地方處理

    }
)
;


export default request;




