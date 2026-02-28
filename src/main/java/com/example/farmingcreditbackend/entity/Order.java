package com.example.farmingcreditbackend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("credit_order")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long storeId;
    private Long farmerId;
    private LocalDate orderDate;
    private LocalDate dueDate;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal debtAmount;
    private LocalDate actualRepayDate;
    private String orderStatus;   // PENDING, APPROVED, REJECTED, PARTIAL_PAID, PAID_OFF, OVERDUE, CANCELLED
    private String paymentStatus; // UNPAID, PARTIAL, PAID
    private Integer overdueDays;
    private BigDecimal overdueFee;
    private Integer seasonalFlag;
    private String invoiceType;
    private String remark;
    private String attachmentIds; // JSON
    private Long creatorId;
    private String creatorName;
    private Long approverId;
    private String approverName;
    private LocalDateTime approveTime;
    private String cancelReason;
    private Long cancellerId;
    private String cancellerName;
    private LocalDateTime cancelTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}