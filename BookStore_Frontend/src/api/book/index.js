import request from '@/utils/request';
import {omitEmpty} from '@/api/params';

/**
 * 搜尋書籍
 * @param {string} cat 分類
 * @param {string} q 關鍵字
 * @param {string} sortBy 排序欄位（book_name、author、price、sales、creation_date）
 */
export const getBooks = (cat, q, sortBy, currentPage, pageSize) => {
    return request.get('/user/books', {params: omitEmpty({cat, q, sortBy, currentPage, pageSize})});
};

/**
 * 獲取前10名銷售量的書籍
 */
export const getTop10SalesBooks = () => {
    return request.get('/user/books/top10-bestseller');
};

/**
 * 獲取前10新上架的書籍
 */
export const getTop10NewBooks = () => {
    return request.get('/user/books/top10-new-releases');
};

/**
 * 透過category獲取前10銷售量的書籍
 */
export const getTop10SalesBooksByCategory = (category) => {
    return request.get(`/user/books/top10-bestseller/${encodeURIComponent(category)}`);
};

/**
 * 獲取前 100名銷售量的書籍
 */
export const getTop100SalesBooks = (cat, q, currentPage, pageSize) => {
    return request.get('/user/books/top100-bestseller', {params: omitEmpty({cat, q, currentPage, pageSize})});
};

/**
 * 獲取前 100名新上架的書籍
 */
export const getTop100NewlyReleasedBooks = (cat, q, currentPage, pageSize) => {
    return request.get('/user/books/top100-new-releases', {params: omitEmpty({cat, q, currentPage, pageSize})});
};

/**
 *透過isbn獲取書籍資料
 */
export const getBookDetail = (isbn) => {
    return request.get(`/user/book/detail/${isbn}`);
};

/**
 *透過ID獲取書籍資料
 */
export const getBookById = (id) => {
    return request.get(`/user/book/search/id/${id}`);
};

/**
 *透過多筆ID獲取多筆書籍資料
 * @param {{ids: Array<number|string>}} payload
 */
export const getBooksById = (payload) => {
    return request.post('/user/book/search/ids', payload);
};
