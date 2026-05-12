package yixian.website.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import yixian.website.dto.BookDto;
import yixian.website.model.Book;
import yixian.website.repository.BookRepository;
import yixian.website.common.ErrorMessages;
import yixian.website.common.Result;
import yixian.website.common.SuccessMessages;
import yixian.website.exception.EntityNotFoundException;
import yixian.website.exception.ServiceException;
import yixian.website.service.query.BookQueryService;

import java.io.IOException;

import static yixian.website.util.CommonUtils.defaultIfNull;
import static yixian.website.util.ImageUtils.processImage;

@Service
public class BookManagementService {
    private final BookRepository bookRepository;
    private final BookQueryService bookQueryService;

    public BookManagementService(BookRepository bookRepository, BookQueryService bookQueryService) {
        this.bookRepository = bookRepository;
        this.bookQueryService = bookQueryService;
    }

    public Book saveOrUpdateBook(BookDto bookDto) {
        Book book = getOrCreateBook(bookDto.getId());
        setBookDetailsFromDto(book, bookDto);
        return saveBook(book);
    }

    private Book getOrCreateBook(Integer bookId) {
        if (bookId == null) {
            return new Book();
        }
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("找不到 bookId 為 " + bookId + " 的書籍"));
    }

    private void setBookDetailsFromDto(Book book, BookDto bookDto) {
        book.setIsbn(defaultIfNull(bookDto.getIsbn(), ""));
        book.setBookName(defaultIfNull(bookDto.getBookName(), ""));
        book.setAuthor(defaultIfNull(bookDto.getAuthor(), ""));
        book.setCategory(defaultIfNull(bookDto.getCategory(), ""));
        book.setPublisher(defaultIfNull(bookDto.getPublisher(), ""));
        book.setPublishedDate(defaultIfNull(bookDto.getPublishedDate(), ""));
        book.setQuantity(defaultIfNull(bookDto.getQuantity(), 0));
        book.setPrice(defaultIfNull(bookDto.getPrice(), 0));
        book.setDescription(defaultIfNull(bookDto.getDescription(), ""));
        processBookCover(book, bookDto);
    }

    //前端使用者上傳的是MultipartFile類型，api提供的是String，所以要分開處理
    private void processBookCover(Book book, BookDto bookDto) {
        if (bookDto.getUploadBookCover() != null) {  //如果使用者有上傳的圖片
            setUserUploadBookCover(bookDto.getUploadBookCover(), book);
        } else if (!bookDto.getApi_bookCoverUrl().isEmpty()) {     //如果使用者沒上傳的圖片，而Api有提供圖片
            setAndProcessBookCoverFromApiUrl(book, bookDto);
        }
    }

    private void setUserUploadBookCover(MultipartFile booKCover, Book book) {
        try {
            book.setBookCoverByte(booKCover.getBytes());
        } catch (IOException e) {
            throw new ServiceException(ErrorMessages.PROCESS_IMG_ERROR, e);
        }
    }

    private void setAndProcessBookCoverFromApiUrl(Book book, BookDto bookDto) {
        book.setBookCoverByte(processImage(bookDto.getApi_bookCoverUrl()));
    }


    private Book saveBook(Book book) {
        return bookRepository.save(book);
    }


    public void softDeleteBookById(Integer id) {
        Book book = bookQueryService.findBookById(id);
        book.setIsDeleted(true);
        bookRepository.save(book);
    }

    public void relistBook(Integer id) {
        Book book = bookQueryService.findBookById(id);
        book.setIsDeleted(false);
        bookRepository.save(book);
    }


}
