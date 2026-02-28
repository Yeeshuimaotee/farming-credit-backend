package com.example.farmingcreditbackend.dto;

import lombok.Data;

@Data
public class CreateOrderResponseDTO {
    private Long orderId;
    private String orderNo;
}