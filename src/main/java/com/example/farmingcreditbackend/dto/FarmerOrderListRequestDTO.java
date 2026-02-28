package com.example.farmingcreditbackend.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class FarmerOrderListRequestDTO {
    private Integer currentPage = 1;
    private Integer size = 10;
    private String status;      // 订单状态：PENDING, APPROVED, OVERDUE, PAID_OFF 等
    private LocalDate startDate;
    private LocalDate endDate;
}