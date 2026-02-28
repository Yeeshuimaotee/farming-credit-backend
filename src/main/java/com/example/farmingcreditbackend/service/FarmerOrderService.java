package com.example.farmingcreditbackend.service;

import com.example.farmingcreditbackend.dto.*;

public interface FarmerOrderService {
    FarmerOrderListResponseDTO getFarmerOrderList(FarmerOrderListRequestDTO request, Long farmerId);
    FarmerOrderDetailDTO getFarmerOrderDetail(Long orderId, Long farmerId);
}