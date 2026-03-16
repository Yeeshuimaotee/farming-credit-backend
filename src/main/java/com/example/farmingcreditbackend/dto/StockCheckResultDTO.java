package com.example.farmingcreditbackend.dto;

import lombok.Data;

@Data
public class StockCheckResultDTO {
    private Long productId;
    private String productName;
    private Integer stock;          // 当前库存
    private Integer required;       // 所需数量
    private Boolean sufficient;     // 是否充足
}