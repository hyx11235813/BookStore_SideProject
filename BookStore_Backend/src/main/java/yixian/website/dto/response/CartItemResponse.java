package yixian.website.dto.response;

/**
 * 購物車中的商品。quantity 為書籍庫存，orderQuantity 為使用者加入購物車的數量。
 */
public record CartItemResponse(
        Integer id,
        String bookName,
        String author,
        byte[] bookCoverByte,
        Integer price,
        Integer quantity,
        Integer orderQuantity) {
}
