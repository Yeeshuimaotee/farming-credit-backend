package com.example.farmingcreditbackend.service;

import com.example.farmingcreditbackend.dto.DashboardStatisticsDTO;

/**
 * 数据统计服务接口
 */
public interface DashboardService {
    
    /**
     * 获取店铺统计看板数据
     */
    DashboardStatisticsDTO getStoreDashboard(Long storeId);
    
    /**
     * 获取销售趋势数据
     */
    Object getSalesTrend(Long storeId, String startDate, String endDate, String type);
    
    /**
     * 获取热销商品排行
     */
    Object getHotProducts(Long storeId, int limit);
    
    /**
     * 获取客户欠款排行
     */
    Object getDebtRanking(Long storeId, int limit);
    
    /**
     * 获取管理员统计看板数据
     */
    Object getAdminDashboard();
    
    /**
     * 获取系统日志列表
     */
    Object getSystemLogs(Integer page, Integer size);
    
    /**
     * 获取店主统计报表数据
     */
    Object getStoreOwnerStatistics(Long storeId, String startDate, String endDate);
    
    /**
     * 获取回款趋势数据
     */
    Object getRepaymentTrend(Long storeId, String startDate, String endDate, String type);
    
    /**
     * 获取客户分析数据
     */
    Object getCustomerAnalysis(Long storeId);
}
