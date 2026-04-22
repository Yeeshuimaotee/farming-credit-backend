package com.example.farmingcreditbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.farmingcreditbackend.entity.CreditEvaluation;
import com.example.farmingcreditbackend.entity.Farmer;
import com.example.farmingcreditbackend.service.AuthService;
import com.example.farmingcreditbackend.service.CreditEvaluationService;
import com.example.farmingcreditbackend.service.StoreService;
import com.example.farmingcreditbackend.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 信用评估控制器
 */
@RestController
@RequestMapping("/store_owner")
@RequiredArgsConstructor
public class CreditEvaluationController {
    
    private final CreditEvaluationService creditEvaluationService;
    private final StoreService storeService;
    private final AuthService authService;
    
    /**
     * 获取农户信用评估历史
     */
    @GetMapping("/farmers/{farmerId}/evaluations")
    public Result<List<CreditEvaluation>> getEvaluationHistory(@PathVariable Long farmerId) {
        List<CreditEvaluation> evaluations = creditEvaluationService.getEvaluationHistory(farmerId);
        return Result.success(evaluations);
    }
    
    /**
     * 获取农户最新评估结果
     */
    @GetMapping("/farmers/{farmerId}/evaluations/latest")
    public Result<CreditEvaluation> getLatestEvaluation(@PathVariable Long farmerId) {
        CreditEvaluation evaluation = creditEvaluationService.getLatestEvaluation(farmerId);
        return Result.success(evaluation);
    }
    
    /**
     * 手动触发信用评估
     */
    @PostMapping("/farmers/{farmerId}/evaluate")
    public Result<CreditEvaluation> manualEvaluate(@PathVariable Long farmerId) {
        Long userId = authService.getCurrentUser().getId();
        Long storeId = storeService.getStoreIdByOwnerId(userId);
        String userName = authService.getCurrentUser().getRealName();
        
        CreditEvaluation evaluation = creditEvaluationService.manualEvaluate(
                farmerId, storeId, userId, userName);
        
        return Result.success(evaluation);
    }
    
    /**
     * 分页查询店铺评估记录
     */
    @GetMapping("/evaluations")
    public Result<Page<CreditEvaluation>> getStoreEvaluations(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        
        Long userId = authService.getCurrentUser().getId();
        Long storeId = storeService.getStoreIdByOwnerId(userId);
        
        Page<CreditEvaluation> page = new Page<>(current, size);
        List<CreditEvaluation> evaluations = creditEvaluationService.getStoreEvaluations(storeId, size);
        page.setRecords(evaluations);
        page.setTotal(evaluations.size());
        
        return Result.success(page);
    }

    /**
     * 获取风险客户列表（信用等级较差或差）
     */
    @GetMapping("/risk-farmers")
    public Result<List<Farmer>> getRiskFarmers() {
        Long userId = authService.getCurrentUser().getId();
        Long storeId = storeService.getStoreIdByOwnerId(userId);
        List<Farmer> riskFarmers = creditEvaluationService.getRiskFarmers(storeId);
        return Result.success(riskFarmers);
    }

    /**
     * 获取信用评分分布统计
     */
    @GetMapping("/credit-distribution")
    public Result<Object> getCreditDistribution() {
        Long userId = authService.getCurrentUser().getId();
        Long storeId = storeService.getStoreIdByOwnerId(userId);
        Object distribution = creditEvaluationService.getCreditDistribution(storeId);
        return Result.success(distribution);
    }
}
