package yixian.website.service.query;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import yixian.website.common.ErrorMessages;
import yixian.website.dto.BookSearchRequestDto;
import yixian.website.dto.SearchBookRequestDto;
import yixian.website.exception.EntityNotFoundException;
import yixian.website.exception.InvalidDataException;
import yixian.website.model.Book;
import yixian.website.repository.BookRepository;

import java.util.List;

import static yixian.website.util.ImageUtils.convertToBase64;

@Service
public class BookQueryService {
    private static final int PAGEABLE_INDEX_OFFSET = 1; //pageable從0開始，所以要-1
    private static final int FIRST_PAGE_INDEX = 0;
    private static final int MAX_TOP_BOOKS = 100;
    private static final String SORT_BY_SALE = "sales";
    private static final String SORT_BY_CREATION_DATE = "creation_date";
    private final BookRepository bookRepository;

    public BookQueryService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book findBookById(Integer id) {
        if (id == null) {
            throw new InvalidDataException(ErrorMessages.PARAM_CANNOT_BE_NULL);
        }
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.DATA_NOT_FOUND));
    }

    public Page<Book> findBooks(SearchBookRequestDto searchParam, Pageable pageable) {
        return bookRepository.searchBooksByParams(
                searchParam.getIsbn(),
                searchParam.getBookName(),
                searchParam.getAuthor(),
                searchParam.getPublisher(),
                searchParam.getCategory(),
                searchParam.getCreationDateStart(),
                searchParam.getCreationDateEnd(),
                searchParam.getEditDateStart(),
                searchParam.getEditDateEnd(),
                searchParam.isDeleted(),
                pageable
        );

    }

    public Page<Book> findBooksByParams(BookSearchRequestDto dto) {
        String sort = validateSortField(dto.getSortBy());
        int currentPage = Math.max(dto.getCurrentPage() - PAGEABLE_INDEX_OFFSET, 0); // 頁碼最小為 0
        int pageSize = Math.max(dto.getPageSize(), 1); //最少要有一頁結果

        Pageable pageable = PageRequest.of(currentPage, pageSize, //pageable從0開始
                Sort.by(Sort.Direction.ASC, sort));

        return bookRepository.findBooksByParams(dto.getCat(), dto.getQ(), pageable);
    }


    public List<Book> findBooksById(BookSearchRequestDto dto) {
        if (dto.getIds() == null || dto.getIds().isEmpty()) {
            throw new InvalidDataException(ErrorMessages.PARAM_CANNOT_BE_NULL);
        }
        List<Book> books = bookRepository.findAllById(dto.getIds());
        if (books == null || books.isEmpty()) {
            throw new EntityNotFoundException("查無資料");
        }

        return books;
    }

    public Book findBookByIsbn(String isbn) {
        Book book = bookRepository.findBookByIsbn(isbn).orElse(null);
        if (book != null) {
            book.setBookCoverUrl(convertToBase64(book.getBookCoverByte()));
        }

        return book;
    }


    //驗證排序方式有沒有符合資料庫設計
    private String validateSortField(String sortField) {
        List<String> allowedSortFields = List.of("book_name", "author", "price", "sales", "creation_date");
        return allowedSortFields.contains(sortField) ? sortField : "book_name";
    }

    public Page<Book> findTop100Bestseller(BookSearchRequestDto dto) {
        return paginateTop100Books(dto, SORT_BY_SALE);
    }

    public Page<Book> findTop100NewRelease(BookSearchRequestDto dto) {
        return paginateTop100Books(dto, SORT_BY_CREATION_DATE);
    }


    private Page<Book> paginateTop100Books(BookSearchRequestDto dto, String sort) {
        Pageable top100Pageable = PageRequest.of(FIRST_PAGE_INDEX, MAX_TOP_BOOKS, Sort.Direction.DESC, sort);
        List<Book> top100Books = bookRepository
                .findTop100ByParams(dto.getCat(), dto.getQ(), top100Pageable)
                .getContent();
        return getPaginatedBooks(top100Books, dto.getCurrentPage(), dto.getPageSize());
    }

    //將找到的資料依照目前的頁數做分頁
    private Page<Book> getPaginatedBooks(List<Book> books, int currentPage, int pageSize) {
        Pageable pageable = PageRequest.of(currentPage - PAGEABLE_INDEX_OFFSET, pageSize);
        int start = Math.min((int) pageable.getOffset(), books.size());
        int end = Math.min(start + pageSize, books.size());
        List<Book> booksForCurrentPage = books.subList(start, end);

        return new PageImpl<>(booksForCurrentPage, pageable, books.size());
    }

    public Page<Book> page(Pageable pageable, Boolean includeDeletedBooks) {
        if (includeDeletedBooks) {
            return bookRepository.findByIsDeletedTrue(pageable);
        } else {
            return bookRepository.findByIsDeletedFalse(pageable);
        }
    }

}
