import request from '@/utils/request';
import {omitEmpty} from '@/api/params';

/**
 *   獲取圖表資料
 **/
export const getChartData = () => {
    return request.get('/admin/chart/data');
};

/**
 *  獲取分頁的書籍資料 (未標記為刪除的)
 **/
export const getBooks = (currentPage, pageSize) => {
    return request.get('/admin/books', {params: {currentPage, pageSize}});
};

/**
 *  獲取分頁的書籍資料 (標記刪除的)
 **/
export const getDeletedBooks = (currentPage, pageSize) => {
    return request.get('/admin/books/deleted', {params: {currentPage, pageSize}});
};

/**
 *  透過ID獲取書籍資料
 **/
export const getBookById = (id) => {
    return request.get(`/admin/book/search/id/${id}`);
};

/**
 *  新增或更新書籍資料
 *  傳送圖片檔案用 FormData，axios 會自動帶上 multipart 的 Content-Type 與 boundary
 **/
export const saveBook = (formData) => {
    return request.post('/admin/book', formData, {
        headers: {'Content-Type': undefined}
    });
};

/**
 *  書籍標記爲刪除
 **/
export const softDeleteBook = (id) => {
    return request.put(`/admin/book/${id}/deleted`);
};

/**
 *  重新上架書籍
 **/
export const relistBook = (id) => {
    return request.put(`/admin/book/${id}/relist`);
};

/**
 *  檢查此ISBN是否已被新增過
 **/
export const searchBookByISBN = (isbn) => {
    return request.get(`/admin/book/search/isbn/${isbn}`);
};

/**
 *  搜尋書籍
 *  @param {Object} condition 搜尋條件（isbn、bookName、author、publisher、category、日期區間、currentPage）
 *  @param {boolean} deleted 是否搜尋已下架的書籍
 **/
export const searchBooks = (condition, deleted = false) => {
    const {
        isbn, bookName, author, publisher, category, creationDateStart,
        creationDateEnd, editDateStart, editDateEnd, currentPage
    } = condition;
    return request.get('/admin/books/search', {
        params: omitEmpty({
            isbn, bookName, author, publisher, category, creationDateStart,
            creationDateEnd, editDateStart, editDateEnd, currentPage,
            deleted: Boolean(deleted)
        })
    });
};

/**
 * 新增，編輯使用者
 **/
export const saveUser = (data) => {
    return request.post('/admin/user', data);
};

/**
 * 獲取所有使用者資料
 **/
export const getAllUsers = (currentPage, pageSize) => {
    return request.get('/admin/user', {params: {currentPage, pageSize}});
};

/**
 * 透過id取得使用者資料
 **/
export const getUserById = (id) => {
    return request.get(`/admin/user/search/id/${id}`);
};

/**
 * 搜尋使用者
 **/
export const searchUsers = (condition) => {
    const {
        name, account, email, role, isActive, creationDateStart, creationDateEnd,
        editDateStart, editDateEnd, currentPage
    } = condition;
    return request.get('/admin/user/search', {
        params: omitEmpty({
            name, account, email, role, isActive, creationDateStart, creationDateEnd,
            editDateStart, editDateEnd, currentPage
        })
    });
};
