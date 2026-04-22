package com.example.farmingcreditbackend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 数据统计看板 DTO
 */
@Data
public class DashboardStatisticsDTO {
    
    /**
     * 销售统计数据
     */
    private SalesStatistics salesStatistics;
    
    /**
     * 回款统计数据
     */
    private RepaymentStatistics repaymentStatistics;
    
    /**
     * 客户统计数据
     */
    private CustomerStatistics customerStatistics;
    
    /**
     * 热销商品排行
     */
    private List<ProductRanking> productRanking;
    
    /**
     * 欠款 TOP 客户
     */
    private List<CustomerDebtRanking> customerDebtRanking;
    
    @Data
    public static class SalesStatistics {
        private BigDecimal todaySales; // 今日销售额
        private BigDecimal weekSales; // 本周销售额
        private BigDecimal monthSales; // 本月销售额
        private BigDecimal yearSales; // 本年销售额
        private BigDecimal creditSalesRatio; // 赊销占比
        private Integer todayOrderCount; // 今日订单数
        private Integer monthOrderCount; // 本月订单数
    }
    
    @Data
    public static class RepaymentStatistics {
        private BigDecimal totalRepayment; // 总回款金额
        private BigDecimal monthRepayment; // 本月回款
        private BigDecimal repaymentRate; // 回款率
        private BigDecimal overdueRate; // 逾期率
        private BigDecimal totalDebt; // 总欠款
        private BigDecimal overdueDebt; // 逾期欠款
    }
    
    @Data
    public static class CustomerStatistics {
        private Integer totalCustomerCount; // 总客户数
        private Integer newCustomerCount; // 新增客户数
        private Integer activeCustomerCount; // 活跃客户数
        private BigDecimal avgCreditScore; // 平均信用评分
        private Map<String, Integer> creditLevelDistribution; // 信用等级分布
    }
    
    @Data
    public static class ProductRanking {
        private Long productId;
        private String productName;
        private Integer salesCount; // 销售数量
        private BigDecimal salesAmount; // 销售金额
    }
    
    @Data
    public static class CustomerDebtRanking {
        private Long farmerId;
        private String farmerName;
        private BigDecimal debtAmount; // 欠款金额
        private Integer overdueDays; // 逾期天数
    }
}
