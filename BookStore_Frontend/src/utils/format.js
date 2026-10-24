/**
 * 書名轉成網址用的 slug（空白換成連字號）
 */
export const slugify = (title) => title.toLowerCase().replace(/ /g, '-');

const pad = (value) => String(value).padStart(2, '0');

/**
 * 格式化成 yyyy-MM-dd HH:mm，沒有值時回傳空字串
 */
export const formatDateTime = (value) => {
    if (!value) return '';
    const date = new Date(value);
    return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`;
};
