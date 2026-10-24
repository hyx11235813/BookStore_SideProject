package yixian.website.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yixian.website.entity.Book;
import yixian.website.entity.BookSale;
import yixian.website.exception.InvalidDataException;
import yixian.website.repository.BookSaleRepository;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class SaleService {
    private final BookSaleRepository bookSaleRepository;
    private final BookService bookService;
    private final UserService userService;

    @Transactional
    public Book sell(Integer userId, Integer bookId) {
        Book book = bookService.findById(bookId);
        if (book.getQuantity() == null || book.getQuantity() <= 0) {
            throw new InvalidDataException("庫存不足");
        }

        BookSale sale = new BookSale();
        sale.setBook(book);
        sale.setUser(userService.findById(userId));
        sale.setSaleDate(new Date());
        sale.setPrice(book.getPrice());
        bookSaleRepository.save(sale);

        book.setSales((book.getSales() == null ? 0 : book.getSales()) + 1);
        book.setQuantity(book.getQuantity() - 1);
        return book;
    }
}
