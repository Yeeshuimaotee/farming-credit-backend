package com.example.farmingcreditbackend.controller;

import com.example.farmingcreditbackend.dto.CreateOrderRequestDTO;
import com.example.farmingcreditbackend.dto.CreateOrderResponseDTO;
import com.example.farmingcreditbackend.dto.OrderListRequestDTO;
import com.example.farmingcreditbackend.dto.OrderListResponseDTO;
import com.example.farmingcreditbackend.dto.OrderResponseDTO;
import com.example.farmingcreditbackend.service.AuthService;
import com.example.farmingcreditbackend.service.OrderService;
import com.example.farmingcreditbackend.service.StoreService;
import com.example.farmingcreditbackend.vo.Result;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/store_owner")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final StoreService storeService;
    private final AuthService authService;

    @PostMapping("/credit-orders")
    public Result<CreateOrderResponseDTO> createCreditOrder(@Valid @RequestBody CreateOrderRequestDTO request) {
        log.info("接收到创建订单请求：{}", request);
        Long userId = authService.getCurrentUser().getId();
        String userName = authService.getCurrentUser().getRealName();
        Long storeId = storeService.getStoreIdByOwnerId(userId);
        log.info("用户 ID: {}, 用户名：{}, 店铺 ID: {}", userId, userName, storeId);
        
        CreateOrderResponseDTO response = orderService.createCreditOrder(request, userId, userName, storeId);
        log.info("订单创建成功，返回数据：orderId={}, orderNo={}", response.getOrderId(), response.getOrderNo());
        return Result.success(response);
    }

    @GetMapping("/credit-orders")
    public Result<OrderListResponseDTO> getCreditOrderList(@ModelAttribute OrderListRequestDTO request) {
        Long userId = authService.getCurrentUser().getId();
        Long storeId = storeService.getStoreIdByOwnerId(userId);
        OrderListResponseDTO response = orderService.getOrderList(request, storeId);
        return Result.success(response);
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/credit-orders/{orderId}")
    public Result<OrderResponseDTO> getOrderDetail(@PathVariable Long orderId) {
        Long userId = authService.getCurrentUser().getId();
        Long storeId = storeService.getStoreIdByOwnerId(userId);
        OrderResponseDTO detail = orderService.getOrderDetail(orderId, storeId);
        return Result.success(detail);
    }

    /**
     * 审核通过订单
     */
    @PostMapping("/credit-orders/{orderId}/approve")
    public Result<Void> approveOrder(@PathVariable Long orderId) {
        String requestId = java.util.UUID.randomUUID().toString().substring(0, 8);
        log.info("========== [审核请求开始] RequestID: {}, OrderId: {}, Time: {}, Thread: {} ==========",
            requestId, orderId, java.time.LocalDateTime.now(), Thread.currentThread().getName());
        
        Long userId = authService.getCurrentUser().getId();
        String userName = authService.getCurrentUser().getRealName();
        
        log.info("[审核请求] RequestID: {}, UserId: {}, UserName: {}", requestId, userId, userName);
        
        try {
            orderService.approveOrder(orderId, userId, userName);
            log.info("========== [审核请求成功] RequestID: {}, OrderId: {}, Time: {} ==========",
                requestId, orderId, java.time.LocalDateTime.now());
            return Result.success();
        } catch (Exception e) {
            log.error("========== [审核请求失败] RequestID: {}, OrderId: {}, Error: {} ==========",
                requestId, orderId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 审核拒绝订单
     */
    @PostMapping("/credit-orders/{orderId}/reject")
    public Result<Void> rejectOrder(
            @PathVariable Long orderId,
            @RequestParam String reason) {
        Long userId = authService.getCurrentUser().getId();
        String userName = authService.getCurrentUser().getRealName();
        orderService.rejectOrder(orderId, userId, userName, reason);
        return Result.success();
    }

    /**
     * 取消订单
     */
    @PostMapping("/credit-orders/{orderId}/cancel")
    public Result<Void> cancelOrder(
            @PathVariable Long orderId,
            @RequestParam String reason) {
        Long userId = authService.getCurrentUser().getId();
        String userName = authService.getCurrentUser().getRealName();
        orderService.cancelOrder(orderId, userId, userName, reason);
        return Result.success();
    }
}