/**
 * 移除沒有值的查詢條件（undefined、null、空字串），讓後端把它們視為「不篩選」
 */
export const omitEmpty = (params) =>
    Object.fromEntries(
        Object.entries(params).filter(([, value]) => value !== undefined && value !== null && value !== '')
    );
