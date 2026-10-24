package yixian.website.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yixian.website.common.Result;
import yixian.website.common.SuccessMessages;
import yixian.website.dto.response.DashboardResponse;
import yixian.website.service.DashboardService;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/chart/data")
    public Result<DashboardResponse> getChartData() {
        return Result.ok(dashboardService.getDashboard(), SuccessMessages.GET_OK);
    }
}
