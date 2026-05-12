import request from '../../utils/request'


/**
 *   獲取圖表資料
 **/
export const getChartData = () => {
    return request.get('/admin/chart/data')
}

/**
 *  獲取所有書籍資料 (未標記為刪除的)
 **/
export const getBooks = (currentPage, pageSize) => {
    return request.get(`/admin/books?currentPage=${currentPage}&pageSize=${pageSize}`)
}

/**
 *  獲取分頁的書籍資料 (標記刪除的)
 **/
export const getDeletedBooks = (currentPage, pageSize) => {
    return request.get(`/admin/books/deleted?currentPage=${currentPage}&pageSize=${pageSize}`)
}
/**
 *  透過ID獲取書籍資料
 **/
export const getBookById = (id) => {
    return request.get(`/admin/book/search/id/${id}`)
}
/**
 *  新增或更新書籍資料
 **/
export const saveBook = (data) => {
    return request.post('/admin/book', data, {
        headers: {
            'Content-Type': 'multipart/editForm-data'   //傳送圖片檔案,更改Content-Type,不然後端@ModelAttribute接收不到
        }

    })
}
/**
 *  書籍標記爲刪除
 **/
export const softDeleteBook = (id) => {
    return request.put(`/admin/book/${id}/deleted`)
}
/**
 *  重新上架書籍
 **/
export const relistBook = (id) => {
    return request.put(`/admin/book/${id}/relist`)
}
/**
 *  檢查此ISBN是否已被新增過
 **/
export const searchBookByISBN = (isbn) => {
    return request.get(`/admin/book/search/isbn/${isbn}`)
}
/**
 *  搜尋書籍
 **/
export const searchBooks = (data, isDeleted) => {
    const {
        isbn, bookName, author, publisher, category, creationDateStart,
        creationDateEnd, editDateStart, editDateEnd, currentPage
    } = data
    const params = buildQueryParams({
        isbn, bookName, author, publisher, category, creationDateStart,
        creationDateEnd, editDateStart, editDateEnd, currentPage,
        deleted: isDeleted ? 'true' : false,
    })
    return request.get(`/admin/books/search?${params}`)
}

/**
 * 新增，編輯使用者
 **/
export const saveUser = (data) => {
    return request.post('/admin/user', data)
}
/**
 * 獲取所有使用者資料
 **/
export const getAllUsers = (currentPage, pageSize) => {
    return request.get(`/admin/user?currentPage=${currentPage}&pageSize=${pageSize}`)
}
/**
 * 透過id取得使用者資料
 **/
export const getUserById = (id) => {
    return request.get(`/admin/user/search/id/${id}`)
}

/**
 * 搜尋使用者
 **/
export const searchUsers = (data) => {
    const {
        account, email, role, status, creationDateStart, creationDateEnd,
        editDateStart, editDateEnd, currentPage
    } = data
    const params = buildQueryParams({
        account, email, role, status, creationDateStart, creationDateEnd,
        editDateStart, editDateEnd, currentPage
    })
    return request.get(`/admin/user/search?${params}`)
}

/**
 * 過濾掉空的資料，並轉換為查詢參數
 **/
const buildQueryParams = (paramsObj) => {
    return new URLSearchParams(
        Object.entries(paramsObj)
            // eslint-disable-next-line no-unused-vars
            .filter(([_, value]) => value !== undefined && value !== null)
    ).toString()
}
