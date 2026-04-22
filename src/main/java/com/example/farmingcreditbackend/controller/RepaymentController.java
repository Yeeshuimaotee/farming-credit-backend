package com.example.farmingcreditbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.farmingcreditbackend.dto.OverdueOrderDTO;
import com.example.farmingcreditbackend.dto.RepaymentListDTO;
import com.example.farmingcreditbackend.entity.Order;
import com.example.farmingcreditbackend.entity.Repayment;
import com.example.farmingcreditbackend.mapper.RepaymentMapper;
import com.example.farmingcreditbackend.service.AuthService;
import com.example.farmingcreditbackend.service.RepaymentService;
import com.example.farmingcreditbackend.service.impl.RepaymentServiceImpl;
import com.example.farmingcreditbackend.service.StoreService;
import com.example.farmingcreditbackend.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 还款管理控制器
 */
@RestController
@RequestMapping("/store_owner/repayments")
@RequiredArgsConstructor
public class RepaymentController {
    
    private final RepaymentService repaymentService;
    private final StoreService storeService;
    private final AuthService authService;
    private final RepaymentMapper repaymentMapper;
    
    /**
     * 获取还款记录列表（分页）
     */
    @GetMapping
    public Result<Page<RepaymentListDTO>> getRepaymentList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long farmerId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        Long userId = authService.getCurrentUser().getId();
        Long storeId = storeService.getStoreIdByOwnerId(userId);
        
        Page<RepaymentListDTO> resultPage = new Page<>(page, size);
        Page<RepaymentListDTO> pageData = repaymentMapper.selectRepaymentListPage(
            resultPage, storeId, farmerId, startDate, endDate);
        
        return Result.success(pageData);
    }
    
    /**
     * 创建还款记录
     */
    @PostMapping
    public Result<Repayment> createRepayment(@RequestBody Repayment repayment) {
        Repayment created = repaymentService.createRepayment(repayment);
        
        // 还款成功后触发信用评估（非事务）
        if (created != null && created.getFarmerId() != null && created.getStoreId() != null) {
            RepaymentServiceImpl repaymentServiceImpl = (RepaymentServiceImpl) repaymentService;
            repaymentServiceImpl.triggerCreditEvaluationAfterRepayment(created.getFarmerId(), created.getStoreId());
        }
        
        return Result.success(created);
    }
    
    /**
     * 根据订单 ID 查询还款记录
     */
    @GetMapping("/order/{orderId}")
    public Result<List<Repayment>> getRepaymentsByOrderId(@PathVariable Long orderId) {
        List<Repayment> repayments = repaymentService.getRepaymentsByOrderId(orderId);
        return Result.success(repayments);
    }
    
    /**
     * 根据农户 ID 查询还款记录
     */
    @GetMapping("/farmer/{farmerId}")
    public Result<Page<RepaymentListDTO>> getRepaymentsByFarmerId(
            @PathVariable Long farmerId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "20") Integer size) {
        
        Page<RepaymentListDTO> page = repaymentService.getRepaymentsByFarmerId(farmerId, current, size);
        return Result.success(page);
    }
    
    /**
     * 获取还款统计信息
     */
    @GetMapping("/statistics")
    public Result<Object> getRepaymentStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        Long userId = authService.getCurrentUser().getId();
        Long storeId = storeService.getStoreIdByOwnerId(userId);
        Object statistics = repaymentService.getRepaymentStatistics(storeId, startDate, endDate);
        return Result.success(statistics);
    }

    /**
     * 获取逾期订单列表
     */
    @GetMapping("/overdue-orders")
    public Result<List<OverdueOrderDTO>> getOverdueOrders(
            @RequestParam(required = false) Integer minOverdueDays) {
        
        Long userId = authService.getCurrentUser().getId();
        Long storeId = storeService.getStoreIdByOwnerId(userId);
        List<OverdueOrderDTO> overdueOrders = repaymentService.getOverdueOrders(storeId, minOverdueDays);
        return Result.success(overdueOrders);
    }

    /**
     * 获取应收账款总览
     */
    @GetMapping("/accounts-receivable")
    public Result<Object> getAccountsReceivable() {
        Long userId = authService.getCurrentUser().getId();
        Long storeId = storeService.getStoreIdByOwnerId(userId);
        Object receivable = repaymentService.getAccountsReceivable(storeId);
        return Result.success(receivable);
    }
}
