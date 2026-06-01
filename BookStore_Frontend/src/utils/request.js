import axios from 'axios';
import router from '@/router';
const request = axios.create({
    headers: {
        'Content-Type': 'application/json;charset=utf-8'
    },
    baseURL: '/api'
    // timeout: 10000
});


request.interceptors.request.use(function (config) {
    config.headers['token'] = JSON.parse(localStorage.getItem('token') || '{}'); //token放入請求頭
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

        return Promise.reject(error); // 其他錯誤，交由呼叫 API 的地方處理

    }
)
;


export default request;




