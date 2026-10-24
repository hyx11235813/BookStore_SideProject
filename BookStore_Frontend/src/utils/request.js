import axios from 'axios';
import router from '@/router';
import {tokenStorage} from '@/utils/storage';

const HTTP_UNAUTHORIZED = 401;
const HTTP_FORBIDDEN = 403;

const request = axios.create({
    headers: {
        'Content-Type': 'application/json;charset=utf-8'
    },
    baseURL: '/api'
});

request.interceptors.request.use((config) => {
    const token = tokenStorage.get();
    if (token) {
        config.headers['token'] = token; //token放入請求頭
    }
    return config;
});

/**
 * 後端所有回應都是 {code, data, message}，HTTP status 與 code 一致。
 * 業務錯誤（4xx）也會當成「已完成的回應」交給呼叫端用 code 判斷，
 * 只有網路中斷、伺服器無回應等情況才會 reject。
 */
const handleResult = (body) => {
    if (body?.code === HTTP_UNAUTHORIZED) {
        tokenStorage.clear(); //token 無效或過期，清除殘留的 token
    }
    if (body?.code === HTTP_FORBIDDEN) {
        console.warn('權限不足');
        router.back();
        return Promise.reject(new Error('403 Forbidden'));
    }
    return body;
};

request.interceptors.response.use(
    (response) => handleResult(response.data),
    (error) => {
        const body = error.response?.data;
        if (body && typeof body.code === 'number') {
            return handleResult(body);
        }
        console.error(error);
        return Promise.reject(error);
    }
);

export default request;
