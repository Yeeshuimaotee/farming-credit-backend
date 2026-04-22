package com.example.farmingcreditbackend.dto;

import com.example.farmingcreditbackend.entity.OrderItem;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单响应 DTO
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
    private String approverName;
    private LocalDateTime approveTime;
    private String cancelReason;
    private Long cancellerId;
    private String cancellerName;
    private LocalDateTime cancelTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<OrderItem> items;
}
