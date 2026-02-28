package com.example.farmingcreditbackend.dto;

import lombok.Data;

import java.util.List;

@Data
public class FarmerOrderListResponseDTO {
    private List<FarmerOrderListDTO> list;
    private Long total;
}