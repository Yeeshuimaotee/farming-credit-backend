package com.example.farmingcreditbackend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OrderListDTO {
    private Long id; // 订单 ID，用于前端操作
    private String orderNo;
    private String farmerName;
    private LocalDate orderDate;
    private BigDecimal totalAmount;
    private BigDecimal debtAmount;
    private LocalDate dueDate;
    private String orderStatus; // 返回英文状态，前端通过 statusTag 映射为中文
}