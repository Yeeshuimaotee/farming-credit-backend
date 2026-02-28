package com.example.farmingcreditbackend.controller;

import com.example.farmingcreditbackend.dto.CreateOrderRequestDTO;
import com.example.farmingcreditbackend.dto.CreateOrderResponseDTO;
import com.example.farmingcreditbackend.dto.OrderListRequestDTO;
import com.example.farmingcreditbackend.dto.OrderListResponseDTO;
import com.example.farmingcreditbackend.service.AuthService;
import com.example.farmingcreditbackend.service.OrderService;
import com.example.farmingcreditbackend.service.StoreService;
import com.example.farmingcreditbackend.vo.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/store_owner")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final StoreService storeService;
    private final AuthService authService;

    @PostMapping("/credit-orders")
    public Result<CreateOrderResponseDTO> createCreditOrder(@Valid @RequestBody CreateOrderRequestDTO request) {
        Long userId = authService.getCurrentUser().getId();
        String userName = authService.getCurrentUser().getRealName();
        Long storeId = storeService.getStoreIdByOwnerId(userId);
        CreateOrderResponseDTO response = orderService.createCreditOrder(request, userId, userName, storeId);
        return Result.success(response);
    }

    @GetMapping("/credit-orders")
    public Result<OrderListResponseDTO> getCreditOrderList(@ModelAttribute OrderListRequestDTO request) {
        Long userId = authService.getCurrentUser().getId();
        Long storeId = storeService.getStoreIdByOwnerId(userId);
        OrderListResponseDTO response = orderService.getOrderList(request, storeId);
        return Result.success(response);
    }
}