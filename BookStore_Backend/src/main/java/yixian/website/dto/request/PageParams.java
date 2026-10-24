package yixian.website.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * 分頁參數。currentPage 從 1 開始，超出範圍的值會被修正。
 */
@Getter
@Setter
public class PageParams {
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 100;

    private Integer currentPage = 1;
    private Integer pageSize = DEFAULT_PAGE_SIZE;

    public Pageable toPageable() {
        return toPageable(Sort.by("id"));
    }

    public Pageable toPageable(Sort sort) {
        return PageRequest.of(pageIndex(), normalizedPageSize(), sort);
    }

    private int pageIndex() {
        return currentPage == null ? 0 : Math.max(currentPage - 1, 0);//pageable從索引0開始，所以要-1
    }

    private int normalizedPageSize() {
        int size = pageSize == null ? DEFAULT_PAGE_SIZE : pageSize;
        return Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
    }
}
