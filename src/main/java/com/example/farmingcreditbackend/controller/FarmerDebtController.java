package com.example.farmingcreditbackend.controller;

import com.example.farmingcreditbackend.dto.FarmerDebtDTO;
import com.example.farmingcreditbackend.service.FarmerDebtService;
import com.example.farmingcreditbackend.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/farmer/debt")
public class FarmerDebtController {
    
    @Autowired
    private FarmerDebtService farmerDebtService;
    
    /**
     * 获取农户欠款总览
     */
    @GetMapping("/overview")
    public Result<FarmerDebtDTO> getDebtOverview(@RequestParam Long farmerId) {
        log.info("获取农户 ID: {} 的欠款总览", farmerId);
        FarmerDebtDTO overview = farmerDebtService.getFarmerDebtOverview(farmerId);
        log.info("返回的最近订单数量：{}", overview.getUpcomingRepayments().size());
        overview.getUpcomingRepayments().forEach(order -> 
            log.info("订单号：{}, 创建时间：{}, 状态：{}", order.getOrderNo(), order.getOrderDate(), order.getOrderStatus())
        );
        return Result.success(overview);
    }
    
    /**
     * 获取农户欠款趋势
     */
    @GetMapping("/trend")
    public Result<Object> getDebtTrend(
            @RequestParam Long farmerId,
            @RequestParam(defaultValue = "6") Integer months) {
        
        Object trend = farmerDebtService.getDebtTrend(farmerId, months);
        return Result.success(trend);
    }
}
