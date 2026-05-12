package yixian.website.service;

import org.springframework.stereotype.Service;
import yixian.website.repository.BookRepository;
import yixian.website.repository.BookSaleRepository;
import yixian.website.common.ErrorMessages;
import yixian.website.exception.ServiceException;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class ChartService {
    private final
    BookRepository bookRepository;
    private final
    BookSaleRepository bookSaleRepository;

    public ChartService(BookRepository bookRepository, BookSaleRepository bookSaleRepository) {
        this.bookRepository = bookRepository;
        this.bookSaleRepository = bookSaleRepository;
    }

    public List<Map<String, String>> fetchCountData() {
        List<Map<String, String>> list = new ArrayList<>();
        ZoneId zoneId = ZoneId.of("Asia/Taipei");
        //獲取今天0點的時間
        Date startOfToday = Date.from(LocalDate.now(zoneId).atStartOfDay(zoneId).toInstant());
        //獲取本週的星期一的開始時間（00:00）
        Date startOfWeek = Date.from(LocalDate.now(zoneId).with(DayOfWeek.MONDAY).atStartOfDay(zoneId).toInstant());
        //通過日期查詢該日期至今新增的數量
        try {
            long todayAdded = bookRepository.countByCreationDateAfter(startOfToday);
            long weekAdded = bookRepository.countByCreationDateAfter(startOfWeek);
            long todaySales = bookSaleRepository.countBySaleDateAfter(startOfToday);
            long weekSales = bookSaleRepository.countBySaleDateAfter(startOfWeek);

            Map<String, String> todayAddedMap = new HashMap<>();
            todayAddedMap.put("title", "今日上架");
            todayAddedMap.put("value", String.valueOf(todayAdded));
            todayAddedMap.put("icon", "el-icon-files");
            list.add(todayAddedMap);

            Map<String, String> todayBorrowedMap = new HashMap<>();
            todayBorrowedMap.put("title", "今日出售");
            todayBorrowedMap.put("value", String.valueOf(todaySales));
            todayBorrowedMap.put("icon", "el-icon-money");
            list.add(todayBorrowedMap);

            Map<String, String> weekAddedMap = new HashMap<>();
            weekAddedMap.put("title", "本周上架");
            weekAddedMap.put("value", String.valueOf(weekAdded));
            weekAddedMap.put("icon", "el-icon-files");
            list.add(weekAddedMap);

            Map<String, String> weekBorrowedMap = new HashMap<>();
            weekBorrowedMap.put("title", "本周出售");
            weekBorrowedMap.put("value", String.valueOf(weekSales));
            weekBorrowedMap.put("icon", "el-icon-money");
            list.add(weekBorrowedMap);
        } catch (Exception e) {
            throw new ServiceException(ErrorMessages.DATA_NOT_FOUND,e);
        }

        return list;
    }


    //獲取bar chart的每月書籍上架及售出數據
    public List<Map<String, String>> fetchMonthlyBarData() {
        List<Map<String, String>> list = new ArrayList<>();
        ZoneId zoneId = ZoneId.of("Asia/Taipei");
        for (int i = 1; i <= 12; i++) {
            //獲取每月1號的日期
            LocalDate startDate = LocalDate.of(LocalDate.now(zoneId).getYear(), i, 1);
            //獲取每月最後一天的日期（加一個月再減一天）
            LocalDate endDate = startDate.plusMonths(1).minusDays(1);
            //轉換為Date對象
            Date startOfMonth = Date.from(startDate.atStartOfDay(zoneId).toInstant());
            Date endOfMonth = Date.from(endDate.atStartOfDay(zoneId).toInstant());
            try {
                long monthlyAddedBooks = bookRepository.countByCreationDateBetween(startOfMonth, endOfMonth);
                long monthlySales = bookSaleRepository.countBySaleDateBetween(startOfMonth, endOfMonth);
                BigDecimal monthlySalesPrice = bookSaleRepository.sumTotalSalePriceBetweenMonths(startOfMonth, endOfMonth);
                Map<String, String> monthAddedMap = new HashMap<>();
                monthAddedMap.put("month", i + "月");
                monthAddedMap.put("monthly_AddedBooks", String.valueOf(monthlyAddedBooks));
                monthAddedMap.put("monthly_Sales", String.valueOf(monthlySales));
                monthAddedMap.put("monthly_SalesPrice", String.valueOf(monthlySalesPrice));
                list.add(monthAddedMap);
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }
        return list;
    }


    //獲取餅圖數據
    public List<Map<String, String>> fetchPieChartData() {
        List<Object[]> list = bookSaleRepository.countBookSaleByCategory();
        List<Map<String, String>> format = new ArrayList<>();
        for (Object[] object : list) {
            Map<String, String> map = new HashMap<>();
            map.put("category", object[0].toString());
            map.put("salesCount", object[1].toString());
            format.add(map);
        }
        return format;
    }
}
