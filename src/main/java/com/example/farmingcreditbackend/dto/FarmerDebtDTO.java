package com.example.farmingcreditbackend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 农户欠款统计 DTO
 */
@Data
public class FarmerDebtDTO {
    
    /**
     * 农户基本信息
     */
    private Long farmerId;
    private String farmerName;
    private String phone;
    
    /**
     * 欠款统计
     */
    private BigDecimal totalDebt; // 总欠款
    private BigDecimal overdueDebt; // 逾期欠款
    private BigDecimal normalDebt; // 正常欠款
    private Integer totalOrderCount; // 总订单数
    private Integer unpaidOrderCount; // 未结清订单数
    private Integer overdueOrderCount; // 逾期订单数
    
    /**
     * 信用状况
     */
    private Integer creditScore; // 信用评分
    private String creditLevel; // 信用等级
    
    /**
     * 近期应还款项
     */
    private List<DebtItem> upcomingRepayments;
    
    /**
     * 欠款趋势（近 6 个月）
     */
    private List<DebtTrend> debtTrend;
    
    /**
     * 欠款明细项
     */
    @Data
    public static class DebtItem {
        private Long orderId;
        private String orderNo;
        private String orderDate;
        private BigDecimal totalAmount; // 订单总额
        private BigDecimal paidAmount; // 已还金额
        private BigDecimal debtAmount; // 欠款金额
        private String dueDate; // 应还日期
        private Integer overdueDays; // 逾期天数
        private String orderStatus; // 订单状态
    }
    
    /**
     * 欠款趋势项
     */
    @Data
    public static class DebtTrend {
        private String month; // 月份
        private BigDecimal debtAmount; // 欠款金额
    }
}
