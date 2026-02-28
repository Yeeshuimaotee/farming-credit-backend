package com.example.farmingcreditbackend.controller;

import com.example.farmingcreditbackend.dto.CreateOrderRequestDTO;
import com.example.farmingcreditbackend.dto.CreateOrderResponseDTO;
import com.example.farmingcreditbackend.service.AuthService;
import com.example.farmingcreditbackend.service.CreditOrderService;
import com.example.farmingcreditbackend.service.StoreService;
import com.example.farmingcreditbackend.vo.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/store_owner")
@RequiredArgsConstructor
public class CreditOrderController {

    private final CreditOrderService creditOrderService;
    private final StoreService storeService;
    private final AuthService authService;

    @PostMapping("/credit-orders")
    public Result<CreateOrderResponseDTO> createCreditOrder(@Valid @RequestBody CreateOrderRequestDTO request) {
        Long userId = authService.getCurrentUser().getId();
        String userName = authService.getCurrentUser().getRealName();
        Long storeId = storeService.getStoreIdByOwnerId(userId);
        CreateOrderResponseDTO response = creditOrderService.createCreditOrder(request, userId, userName, storeId);
        return Result.success(response);
    }
}