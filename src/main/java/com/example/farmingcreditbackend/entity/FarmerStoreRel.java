package com.example.farmingcreditbackend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("farmer_store_rel")
public class FarmerStoreRel {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long farmerId;
    private Long storeId;
    private BigDecimal creditLimit;
    private BigDecimal currentDebt;
    private Integer totalTransactions;
    private BigDecimal totalTransactionAmount;
    private LocalDate firstTransactionDate;
    private LocalDate lastTransactionDate;
    private Integer isPrimary;
    private String relationshipType;
    private String remark;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}