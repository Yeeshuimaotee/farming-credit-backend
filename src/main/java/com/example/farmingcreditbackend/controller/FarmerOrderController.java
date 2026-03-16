package com.example.farmingcreditbackend.controller;

import com.example.farmingcreditbackend.dto.FarmerOrderDetailDTO;
import com.example.farmingcreditbackend.dto.FarmerOrderListRequestDTO;
import com.example.farmingcreditbackend.dto.FarmerOrderListResponseDTO;
import com.example.farmingcreditbackend.dto.OrderRepaymentRecordDTO;
import com.example.farmingcreditbackend.entity.Farmer;
import com.example.farmingcreditbackend.service.AuthService;
import com.example.farmingcreditbackend.service.FarmerOrderService;
import com.example.farmingcreditbackend.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/farmer")
@RequiredArgsConstructor
public class FarmerOrderController {

    private final FarmerOrderService farmerOrderService;
    private final AuthService authService;

    /**
     * 获取当前农户的订单列表
     */
    @GetMapping("/orders")
    @PreAuthorize("hasRole('FARMER')")
    public Result<FarmerOrderListResponseDTO> getOrderList(@ModelAttribute FarmerOrderListRequestDTO request) {
        // 获取当前登录的农户信息
        Farmer farmer = authService.getCurrentFarmer(); // 需要 AuthService 提供此方法
        if (farmer == null) {
            return Result.error("农户信息不存在");
        }
        FarmerOrderListResponseDTO response = farmerOrderService.getFarmerOrderList(request, farmer.getId());
        return Result.success(response);
    }

    /**
     * 获取当前农户的订单详情
     */
    @GetMapping("/orders/{id}")
    @PreAuthorize("hasRole('FARMER')")
    public Result<FarmerOrderDetailDTO> getOrderDetail(@PathVariable Long id) {
        Farmer farmer = authService.getCurrentFarmer();
        if (farmer == null) {
            return Result.error("农户信息不存在");
        }
        FarmerOrderDetailDTO detail = farmerOrderService.getFarmerOrderDetail(id, farmer.getId());
        return Result.success(detail);
    }

    /**
     * 获取当前农户的订单还款记录
     */
    @GetMapping("/orders/{orderId}/repayments")
    @PreAuthorize("hasRole('FARMER')")
    public Result<List<OrderRepaymentRecordDTO>> getOrderRepayments(@PathVariable Long orderId) {
        Farmer farmer = authService.getCurrentFarmer();
        if (farmer == null) {
            return Result.error("农户信息不存在");
        }
        List<OrderRepaymentRecordDTO> list = farmerOrderService.getOrderRepayments(orderId, farmer.getId());
        return Result.success(list);
    }
}