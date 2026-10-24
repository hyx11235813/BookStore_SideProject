package yixian.website.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import yixian.website.common.ErrorMessages;
import yixian.website.dto.request.BookIdsRequest;
import yixian.website.dto.request.BookSaveRequest;
import yixian.website.dto.request.BookSearchRequest;
import yixian.website.dto.request.PageParams;
import yixian.website.dto.request.StoreBookQuery;
import yixian.website.entity.Book;
import yixian.website.exception.EntityNotFoundException;
import yixian.website.exception.InvalidDataException;
import yixian.website.exception.ServiceException;
import yixian.website.repository.BookRepository;
import yixian.website.util.RemoteImageDownloader;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {
    private static final int MAX_TOP_BOOKS = 100;
    private static final String DEFAULT_SORT_FIELD = "book_name";
    //使用者可以選擇的排序欄位（native query 直接使用欄位名稱，所以一定要白名單驗證）
    private static final List<String> ALLOWED_SORT_FIELDS = List.of(DEFAULT_SORT_FIELD, "author", "price", "sales", "creation_date");
    private static final String SORT_BY_SALES = "sales";
    private static final String SORT_BY_CREATION_DATE = "creation_date";

    private final BookRepository bookRepository;
    private final RemoteImageDownloader remoteImageDownloader;

    public Book findById(Integer id) {
        if (id == null) {
            throw new InvalidDataException(ErrorMessages.PARAM_CANNOT_BE_NULL);
        }
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.DATA_NOT_FOUND));
    }

    public Optional<Book> findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    public List<Book> findByIds(BookIdsRequest request) {
        List<Book> books = bookRepository.findAllById(request.ids());
        if (books.isEmpty()) {
            throw new EntityNotFoundException("查無資料");
        }
        return books;
    }

    public Page<Book> findPage(PageParams page, boolean deleted) {
        return bookRepository.findByIsDeleted(deleted, page.toPageable());
    }

    public Page<Book> searchForAdmin(BookSearchRequest search, PageParams page) {
        return bookRepository.searchBooksByParams(
                search.getIsbn(),
                search.getBookName(),
                search.getAuthor(),
                search.getPublisher(),
                search.getCategory(),
                search.getCreationDateStart(),
                search.getCreationDateEnd(),
                search.getEditDateStart(),
                search.getEditDateEnd(),
                search.isDeleted(),
                page.toPageable());
    }

    public Page<Book> searchForStore(StoreBookQuery query) {
        Sort sort = Sort.by(Sort.Direction.ASC, validateSortField(query.getSortBy()));
        return bookRepository.searchPublicBooks(query.getCat(), query.getQ(), query.toPageable(sort));
    }

    public Page<Book> findTop100Bestsellers(StoreBookQuery query) {
        return paginateTop100(query, SORT_BY_SALES);
    }

    public Page<Book> findTop100NewReleases(StoreBookQuery query) {
        return paginateTop100(query, SORT_BY_CREATION_DATE);
    }

    public List<Book> findTop10Bestsellers() {
        return bookRepository.findTop10ByIsDeletedFalseOrderBySalesDesc();
    }

    public List<Book> findTop10NewReleases() {
        return bookRepository.findTop10ByIsDeletedFalseOrderByCreationDateDesc();
    }

    public List<Book> findTop10BestsellersByCategory(String category) {
        return bookRepository.findTop10ByCategoryAndIsDeletedFalseOrderBySalesDesc(category);
    }

    /**
     * 新增或更新書籍
     */
    @Transactional
    public Book save(BookSaveRequest request) {
        Book book = request.getId() == null ? new Book() : findById(request.getId());
        book.setIsbn(Objects.requireNonNullElse(request.getIsbn(), ""));
        book.setBookName(Objects.requireNonNullElse(request.getBookName(), ""));
        book.setAuthor(Objects.requireNonNullElse(request.getAuthor(), ""));
        book.setCategory(Objects.requireNonNullElse(request.getCategory(), ""));
        book.setPublisher(Objects.requireNonNullElse(request.getPublisher(), ""));
        book.setPublishedDate(Objects.requireNonNullElse(request.getPublishedDate(), ""));
        book.setQuantity(Objects.requireNonNullElse(request.getQuantity(), 0));
        book.setPrice(Objects.requireNonNullElse(request.getPrice(), 0));
        book.setDescription(Objects.requireNonNullElse(request.getDescription(), ""));
        applyCover(book, request);
        return bookRepository.save(book);
    }

    @Transactional
    public void softDelete(Integer id) {
        findById(id).setIsDeleted(true);
    }

    @Transactional
    public void relist(Integer id) {
        findById(id).setIsDeleted(false);
    }

    //使用者上傳的是MultipartFile，外部 API 提供的是網址，所以要分開處理
    private void applyCover(Book book, BookSaveRequest request) {
        MultipartFile upload = request.getUploadBookCover();
        if (upload != null && !upload.isEmpty()) {
            try {
                book.setBookCoverByte(upload.getBytes());
            } catch (IOException e) {
                throw new ServiceException(ErrorMessages.PROCESS_IMG_ERROR, e);
            }
        } else if (request.getCoverUrl() != null && !request.getCoverUrl().isBlank()) {
            book.setBookCoverByte(remoteImageDownloader.download(request.getCoverUrl()));
        }
    }

    private String validateSortField(String sortField) {
        return ALLOWED_SORT_FIELDS.contains(sortField) ? sortField : DEFAULT_SORT_FIELD;
    }

    //先取出排行前 100 名，再依照目前的頁數切出該頁的資料
    private Page<Book> paginateTop100(StoreBookQuery query, String sortField) {
        Pageable topBooks = PageRequest.of(0, MAX_TOP_BOOKS, Sort.Direction.DESC, sortField);
        List<Book> top100 = bookRepository.searchPublicBooks(query.getCat(), query.getQ(), topBooks).getContent();

        Pageable requested = query.toPageable(Sort.unsorted());
        int start = (int) Math.min(requested.getOffset(), top100.size());
        int end = Math.min(start + requested.getPageSize(), top100.size());
        return new PageImpl<>(top100.subList(start, end), requested, top100.size());
    }
}
