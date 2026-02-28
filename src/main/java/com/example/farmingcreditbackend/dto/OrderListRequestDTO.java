package com.example.farmingcreditbackend.dto;

import lombok.Data;

@Data
public class OrderListRequestDTO {
    private Integer currentPage = 1;
    private Integer size = 10;
    private String farmerName;
    private String status; // 订单状态：PENDING, APPROVED, OVERDUE, PAID_OFF 等
}