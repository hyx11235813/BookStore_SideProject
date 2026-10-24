package yixian.website.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yixian.website.dto.response.DashboardResponse;
import yixian.website.dto.response.DashboardResponse.MonthlyStat;
import yixian.website.dto.response.DashboardResponse.StatCard;
import yixian.website.repository.BookRepository;
import yixian.website.repository.BookSaleRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {
    private static final ZoneId TAIPEI = ZoneId.of("Asia/Taipei");
    private static final String BOOK_ICON = "el-icon-files";
    private static final String MONEY_ICON = "el-icon-money";

    private final BookRepository bookRepository;
    private final BookSaleRepository bookSaleRepository;

    public DashboardResponse getDashboard() {
        return new DashboardResponse(fetchCountData(), fetchMonthlyData(), bookSaleRepository.countSalesByCategory());
    }

    //今日、本週的上架與出售數量
    private List<StatCard> fetchCountData() {
        LocalDate today = LocalDate.now(TAIPEI);
        Date startOfToday = startOfDay(today);
        Date startOfWeek = startOfDay(today.with(DayOfWeek.MONDAY));

        return List.of(
                new StatCard("今日上架", bookRepository.countByCreationDateGreaterThanEqual(startOfToday), BOOK_ICON),
                new StatCard("今日出售", bookSaleRepository.countBySaleDateGreaterThanEqual(startOfToday), MONEY_ICON),
                new StatCard("本周上架", bookRepository.countByCreationDateGreaterThanEqual(startOfWeek), BOOK_ICON),
                new StatCard("本周出售", bookSaleRepository.countBySaleDateGreaterThanEqual(startOfWeek), MONEY_ICON));
    }

    //今年每個月份的上架數、出售數與銷售額
    private List<MonthlyStat> fetchMonthlyData() {
        int year = LocalDate.now(TAIPEI).getYear();
        return IntStream.rangeClosed(1, 12)
                .mapToObj(month -> {
                    LocalDate firstDay = LocalDate.of(year, month, 1);
                    Date from = startOfDay(firstDay);
                    Date to = startOfDay(firstDay.plusMonths(1));
                    return new MonthlyStat(
                            month + "月",
                            bookRepository.countCreatedBetween(from, to),
                            bookSaleRepository.countSoldBetween(from, to),
                            bookSaleRepository.sumSalePriceBetween(from, to));
                })
                .toList();
    }

    private Date startOfDay(LocalDate date) {
        return Date.from(date.atStartOfDay(TAIPEI).toInstant());
    }
}
