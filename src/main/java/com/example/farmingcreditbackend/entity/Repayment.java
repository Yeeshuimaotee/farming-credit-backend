package com.example.farmingcreditbackend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 还款记录表实体
 */
@Data
@TableName("repayment")
public class Repayment {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String repaymentNo;
    private Long orderId;
    private Long farmerId;
    private Long storeId;
    private LocalDate repaymentDate;
    private BigDecimal repaymentAmount;
    private String repaymentType;
    private BigDecimal beforeDebt;
    private BigDecimal afterDebt;
    private Integer isOverdue;
    private Integer overdueDays;
    private BigDecimal overdueFee;
    private String remark;
    private String attachmentIds;
    private Long operatorId;
    private String operatorName;
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}