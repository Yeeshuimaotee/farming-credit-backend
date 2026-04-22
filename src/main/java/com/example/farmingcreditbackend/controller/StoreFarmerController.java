package com.example.farmingcreditbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.farmingcreditbackend.dto.FarmerCreditDTO;
import com.example.farmingcreditbackend.dto.FarmerCreditInfoDTO;
import com.example.farmingcreditbackend.dto.FarmerSelectDTO;
import com.example.farmingcreditbackend.entity.CreditEvaluation;
import com.example.farmingcreditbackend.entity.Farmer;
import com.example.farmingcreditbackend.mapper.FarmerMapper;
import com.example.farmingcreditbackend.service.AuthService;
import com.example.farmingcreditbackend.service.CreditEvaluationService;
import com.example.farmingcreditbackend.service.StoreFarmerService;
import com.example.farmingcreditbackend.service.StoreService;
import com.example.farmingcreditbackend.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/store_owner/farmers")
@RequiredArgsConstructor
public class StoreFarmerController {

    private final FarmerMapper farmerMapper;
    private final StoreFarmerService storeFarmerService;
    private final StoreService storeService;
    private final AuthService authService;
    private final CreditEvaluationService creditEvaluationService;

    @GetMapping("/select")
    public Result<List<FarmerSelectDTO>> getFarmerSelect() {
        Long userId = authService.getCurrentUser().getId();
        Long storeId = storeService.getStoreIdByOwnerId(userId);
        List<FarmerSelectDTO> list = farmerMapper.selectByStoreId(storeId);
        return Result.success(list);
    }

    /**
     * 获取农户列表（分页）
     */
    @GetMapping
    public Result<Page<Farmer>> getFarmerList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String farmerName,
            @RequestParam(required = false) String creditLevel) {
        
        Long userId = authService.getCurrentUser().getId();
        Long storeId = storeService.getStoreIdByOwnerId(userId);
        
        Page<Farmer> farmerPage = new Page<>(page, size);
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Farmer> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        
        if (farmerName != null && !farmerName.isEmpty()) {
            queryWrapper.like("farmer_name", farmerName);
        }
        
        if (creditLevel != null && !creditLevel.isEmpty()) {
            queryWrapper.eq("credit_level", creditLevel);
        }
        
        queryWrapper.orderByDesc("create_time");
        Page<Farmer> result = farmerMapper.selectPage(farmerPage, queryWrapper);
        
        return Result.success(result);
    }

    /**
     * 获取农户详情
     */
    @GetMapping("/{farmerId}")
    public Result<Farmer> getFarmerDetail(@PathVariable Long farmerId) {
        Farmer farmer = farmerMapper.selectById(farmerId);
        if (farmer == null) {
            return Result.error("农户不存在");
        }
        return Result.success(farmer);
    }

    /**
     * 获取农户信用额度信息
     */
    @GetMapping("/{farmerId}/credit-info")
    public Result<FarmerCreditInfoDTO> getFarmerCreditInfo(@PathVariable Long farmerId) {
        Long userId = authService.getCurrentUser().getId();
        Long storeId = storeService.getStoreIdByOwnerId(userId);
        FarmerCreditInfoDTO creditInfo = storeFarmerService.getFarmerCreditInfo(farmerId, storeId);
        return Result.success(creditInfo);
    }

    /**
     * 设置农户信用额度
     */
    @PutMapping("/{farmerId}/credit")
    public Result<Void> setFarmerCredit(@PathVariable Long farmerId, @RequestBody FarmerCreditDTO creditDTO) {
        Long userId = authService.getCurrentUser().getId();
        Long storeId = storeService.getStoreIdByOwnerId(userId);
        storeFarmerService.setFarmerCredit(farmerId, storeId, creditDTO);
        return Result.success();
    }
}