package com.example.farmingcreditbackend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class FarmerOrderListDTO {
    private Long id;
    private String orderNo;
    private LocalDate orderDate;
    private String storeName;
    private BigDecimal totalAmount;
    private BigDecimal debtAmount;
    private LocalDate dueDate;
    private String orderStatus;
    private String remark;
}