package yixian.website.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import yixian.website.repository.BookRepository;
import yixian.website.repository.BookSaleRepository;
import yixian.website.exception.ServiceException;
import yixian.website.model.Book;
import yixian.website.model.BookSale;
import yixian.website.model.Users;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Service
public class SaleService {
    private final BookSaleRepository bookSaleRepository;
    private final BookRepository bookRepository;
    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Taipei"));
    Date dateInTaipei = calendar.getTime();

    public SaleService(BookSaleRepository bookSaleRepository, BookRepository bookRepository) {
        this.bookSaleRepository = bookSaleRepository;
        this.bookRepository = bookRepository;
    }


    @Transactional
    public boolean sale(Users user, Book book) {
        try {
            BookSale bookSale = new BookSale();
            bookSale.setBook(book);
            bookSale.setUser(user);
            bookSale.setSaleDate(dateInTaipei);
            bookSale.setPrice(book.getPrice());

            user.getBookSales().add(bookSale);
            book.getBookSales().add(bookSale);

            bookSaleRepository.save(bookSale);

            updateBookSales(book);

            return true;
        } catch (Exception e) {
            System.out.println(e);
            throw new ServiceException("出售失敗");
        }
    }
    private void updateBookSales(Book book) {
        int sales = (book.getSales() != null) ? book.getSales() : 0;
        book.setSales(sales + 1);
        book.setQuantity(book.getQuantity() - 1);
        bookRepository.save(book);
    }
}