package com.example.farmingcreditbackend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 订单响应DTO
 */
@Data
public class OrderResponseDTO {
    private Long id;
    private String orderNo;
    private Long storeId;
    private Long farmerId;
    private LocalDate orderDate;
    private LocalDate dueDate;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal debtAmount;
    private String orderStatus;
    private String paymentStatus;
    private Integer overdueDays;
    private BigDecimal overdueFee;
    private Integer seasonalFlag;
    private String invoiceType;
    private String remark;
    private String attachmentIds;
    private Long creatorId;
    private String creatorName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
