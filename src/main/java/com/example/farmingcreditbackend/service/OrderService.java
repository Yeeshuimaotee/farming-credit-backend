package com.example.farmingcreditbackend.service;

import com.example.farmingcreditbackend.dto.CreateOrderRequestDTO;
import com.example.farmingcreditbackend.dto.CreateOrderResponseDTO;
import com.example.farmingcreditbackend.dto.OrderListRequestDTO;
import com.example.farmingcreditbackend.dto.OrderListResponseDTO;
import com.example.farmingcreditbackend.dto.OrderResponseDTO;

public interface OrderService {
    CreateOrderResponseDTO createCreditOrder(CreateOrderRequestDTO request, Long userId, String userName, Long storeId);
    OrderListResponseDTO getOrderList(OrderListRequestDTO request, Long storeId);
    OrderResponseDTO getOrderDetail(Long orderId, Long storeId);
    void approveOrder(Long orderId, Long userId, String userName);
    void rejectOrder(Long orderId, Long userId, String userName, String reason);
    void cancelOrder(Long orderId, Long userId, String userName, String reason);
}