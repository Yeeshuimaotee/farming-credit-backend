package com.example.farmingcreditbackend.controller;

import com.example.farmingcreditbackend.dto.DashboardStatisticsDTO;
import com.example.farmingcreditbackend.service.AuthService;
import com.example.farmingcreditbackend.service.DashboardService;
import com.example.farmingcreditbackend.service.StoreService;
import com.example.farmingcreditbackend.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 数据统计看板控制器
 */
@RestController
@RequestMapping("/store_owner/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    
    private final DashboardService dashboardService;
    private final StoreService storeService;
    private final AuthService authService;
    
    /**
     * 获取店铺统计看板数据
     */
    @GetMapping
    public Result<DashboardStatisticsDTO> getDashboard() {
        Long userId = authService.getCurrentUser().getId();
        Long storeId = storeService.getStoreIdByOwnerId(userId);
        DashboardStatisticsDTO statistics = dashboardService.getStoreDashboard(storeId);
        return Result.success(statistics);
    }
    
    /**
     * 获取销售趋势数据
     */
    @GetMapping("/sales-trend")
    public Result<Object> getSalesTrend(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "day") String type) {
        
        Long userId = authService.getCurrentUser().getId();
        Long storeId = storeService.getStoreIdByOwnerId(userId);
        Object trend = dashboardService.getSalesTrend(storeId, startDate, endDate, type);
        return Result.success(trend);
    }
    
    /**
     * 获取热销商品排行
     */
    @GetMapping("/hot-products")
    public Result<Object> getHotProducts(
            @RequestParam(defaultValue = "10") Integer limit) {
        
        Long userId = authService.getCurrentUser().getId();
        Long storeId = storeService.getStoreIdByOwnerId(userId);
        Object products = dashboardService.getHotProducts(storeId, limit);
        return Result.success(products);
    }
    
    /**
     * 获取客户欠款排行
     */
    @GetMapping("/debt-ranking")
    public Result<Object> getDebtRanking(
            @RequestParam(defaultValue = "10") Integer limit) {
        
        Long userId = authService.getCurrentUser().getId();
        Long storeId = storeService.getStoreIdByOwnerId(userId);
        Object ranking = dashboardService.getDebtRanking(storeId, limit);
        return Result.success(ranking);
    }
    
    /**
     * 获取回款趋势数据
     */
    @GetMapping("/repayment-trend")
    public Result<Object> getRepaymentTrend(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "day") String type) {
        
        Long userId = authService.getCurrentUser().getId();
        Long storeId = storeService.getStoreIdByOwnerId(userId);
        Object trend = dashboardService.getRepaymentTrend(storeId, startDate, endDate, type);
        return Result.success(trend);
    }

    /**
     * 获取客户分析数据
     */
    @GetMapping("/customer-analysis")
    public Result<Object> getCustomerAnalysis() {
        Long userId = authService.getCurrentUser().getId();
        Long storeId = storeService.getStoreIdByOwnerId(userId);
        Object analysis = dashboardService.getCustomerAnalysis(storeId);
        return Result.success(analysis);
    }
}
