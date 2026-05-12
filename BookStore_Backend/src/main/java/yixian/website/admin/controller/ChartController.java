package yixian.website.admin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yixian.website.common.Result;
import yixian.website.service.ChartService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class ChartController {
    private final ChartService chartService;

    public ChartController(ChartService chartService) {
        this.chartService = chartService;
    }


    //TODO 之後可能需要根據前端做調整
    @GetMapping("/chart/data")
    public Result getChartData() {
        List<Map<String, String>> countData = chartService.fetchCountData();
        List<Map<String, String>> barData = chartService.fetchMonthlyBarData();
        List<Map<String, String>> pieData = chartService.fetchPieChartData();
        Map<String, Object> map = new HashMap<>();
        map.put("countData", countData);
        map.put("barData", barData);
        map.put("pieData", pieData);
        return new Result(HttpStatus.OK.value(), map, map.isEmpty() ? "沒有獲取到資料" : "獲取到資料");
    }
}
