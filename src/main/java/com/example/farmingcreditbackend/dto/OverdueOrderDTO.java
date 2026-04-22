package com.example.farmingcreditbackend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 逾期订单 DTO
 */
@Data
public class OverdueOrderDTO {
    private Long id;
    private String orderNo;
    private Long farmerId;
    private String farmerName;      // 农户姓名
    private String farmerPhone;     // 联系电话
    private BigDecimal debtAmount;  // 欠款金额
    private LocalDate dueDate;      // 应还日期
    private Integer overdueDays;    // 逾期天数
    private String orderStatus;     // 订单状态
    private BigDecimal totalAmount; // 订单总额
    private BigDecimal paidAmount;  // 已还金额
    private String reminderStatus;  // 提醒状态：NOT_SENT-未提醒，SENT-已发出提醒，READ-已接收
}
