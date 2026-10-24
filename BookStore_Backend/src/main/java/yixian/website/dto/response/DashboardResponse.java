package yixian.website.dto.response;

import java.util.List;

/**
 * 管理端首頁的統計資料
 */
public record DashboardResponse(List<StatCard> countData, List<MonthlyStat> barData, List<CategorySalesResponse> pieData) {

    public record StatCard(String title, long value, String icon) {
    }

    public record MonthlyStat(String month, long monthlyAddedBooks, long monthlySales, long monthlySalesPrice) {
    }
}
