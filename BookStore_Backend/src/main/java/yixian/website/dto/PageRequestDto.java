package yixian.website.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
@Getter
@Setter


public class PageRequestDto {
    private Integer currentPage;
    private Integer pageSize;

    //Default
    public PageRequestDto() {
        this.currentPage = 1;
        this.pageSize = 10;
    }

    public PageRequestDto(Integer currentPage, Integer pageSize) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    public Pageable toPageable() {
        return PageRequest.of(getPageIndex(), getPageSize());
    }

    private int getPageIndex() {
        if (currentPage <= 0) {
            currentPage = 0;   //避免低於0報錯
        } else {
            currentPage -= 1;//pageable從索引0開始，所以要-1
        }
        return currentPage;
    }

    private int getPageSize() {
        return pageSize;
    }

    public boolean includeDeletedBook() {
        return true;
    }

    public boolean withoutDeletedBook() {
        return false;
    }

}
