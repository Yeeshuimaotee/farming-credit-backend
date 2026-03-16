package com.example.farmingcreditbackend.service;

import com.example.farmingcreditbackend.dto.*;

import java.util.List;

public interface FarmerOrderService {
    FarmerOrderListResponseDTO getFarmerOrderList(FarmerOrderListRequestDTO request, Long farmerId);
    FarmerOrderDetailDTO getFarmerOrderDetail(Long orderId, Long farmerId);
    List<OrderRepaymentRecordDTO> getOrderRepayments(Long orderId, Long farmerId);
}