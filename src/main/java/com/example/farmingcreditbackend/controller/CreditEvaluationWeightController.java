package com.example.farmingcreditbackend.controller;

import com.example.farmingcreditbackend.entity.CreditEvaluationWeight;
import com.example.farmingcreditbackend.service.AuthService;
import com.example.farmingcreditbackend.service.CreditEvaluationWeightService;
import com.example.farmingcreditbackend.service.StoreService;
import com.example.farmingcreditbackend.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 信用评估权重配置控制器
 */
@RestController
@RequestMapping("/store_owner/credit-eval-config")
@RequiredArgsConstructor
public class CreditEvaluationWeightController {

    private final CreditEvaluationWeightService weightService;
    private final AuthService authService;
    private final StoreService storeService;

    /**
     * 获取店铺的信用评估权重配置
     */
    @GetMapping
    public Result<CreditEvaluationWeight> getWeightConfig() {
        Long userId = authService.getCurrentUser().getId();
        Long storeId = storeService.getStoreIdByOwnerId(userId);
        CreditEvaluationWeight weight = weightService.getWeightByStoreId(storeId);
        return Result.success(weight);
    }

    /**
     * 保存店铺的信用评估权重配置
     */
    @PostMapping
    public Result<CreditEvaluationWeight> saveWeightConfig(@RequestBody CreditEvaluationWeight weight) {
        Long userId = authService.getCurrentUser().getId();
        Long storeId = storeService.getStoreIdByOwnerId(userId);
        weight.setStoreId(storeId);
        weight.setCreatedBy(userId);
        weight.setUpdatedBy(userId);
        CreditEvaluationWeight savedWeight = weightService.saveOrUpdateWeight(weight);
        return Result.success(savedWeight);
    }

    /**
     * 获取默认权重配置
     */
    @GetMapping("/default")
    public Result<CreditEvaluationWeight> getDefaultConfig() {
        CreditEvaluationWeight defaultWeight = weightService.getDefaultWeight();
        return Result.success(defaultWeight);
    }
}
