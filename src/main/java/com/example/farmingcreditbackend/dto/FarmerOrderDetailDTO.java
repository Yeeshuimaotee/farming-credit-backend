package com.example.farmingcreditbackend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class FarmerOrderDetailDTO {
    private String orderNo;
    private LocalDate orderDate;
    private String storeName;
    private BigDecimal totalAmount;
    private BigDecimal debtAmount;
    private LocalDate dueDate;
    private String orderStatus;
    private String remark;
    private LocalDateTime createTime;
    private List<OrderItemDetail> items;

    @Data
    public static class OrderItemDetail {
        private String productName;
        private String specification;
        private Integer quantity;
        private BigDecimal price;
        private BigDecimal amount;
    }
}