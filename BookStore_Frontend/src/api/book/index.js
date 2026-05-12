import request from '@/utils/request';


export const getBooks = (cat, q, sort, currentPage, pageSize) => {
    const params = buildQueryParams({cat, q, sort, currentPage, pageSize});
    return request.get(`/user/books?${params}`);
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
    return request.get(`/user/books/top10-bestseller/${category}`);
};
/**
 * 獲取前 100名銷售量的書籍
 */
export const getTop100SalesBooks = (cat, q, currentPage, pageSize) => {
    const params = buildQueryParams({cat, q, currentPage, pageSize});
    return request.get(`/user/books/top100-bestseller?${params}`);
};
/**
 * 獲取前 100名新上架的書籍
 */
export const getTop100NewlyReleasedBooks = (cat, q, currentPage, pageSize) => {
    const params = buildQueryParams({cat, q, currentPage, pageSize});
    return request.get(`/user/books/top100-new-releases?${params}`);
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
 */
export const getBooksById = (ids) => {
    return request.post(`/user/book/search/ids`, ids);
};


//過濾掉空的資料，並轉換為查詢參數
const buildQueryParams = (paramsObj) => {
    return new URLSearchParams(
        Object.entries(paramsObj)
            .filter(([_, value]) => value !== undefined && value !== null)
    ).toString();
};

