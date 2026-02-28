package com.example.farmingcreditbackend.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderListResponseDTO {
    private List<OrderListDTO> list;
    private Long total;
}