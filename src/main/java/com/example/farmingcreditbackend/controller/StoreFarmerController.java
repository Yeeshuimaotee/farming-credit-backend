package com.example.farmingcreditbackend.controller;

import com.example.farmingcreditbackend.dto.FarmerCreditInfoDTO;
import com.example.farmingcreditbackend.dto.FarmerSelectDTO;
import com.example.farmingcreditbackend.mapper.FarmerMapper;
import com.example.farmingcreditbackend.service.AuthService;
import com.example.farmingcreditbackend.service.StoreFarmerService;
import com.example.farmingcreditbackend.service.StoreService;
import com.example.farmingcreditbackend.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @GetMapping("/select")
    public Result<List<FarmerSelectDTO>> getFarmerSelect() {
        Long userId = authService.getCurrentUser().getId();
        Long storeId = storeService.getStoreIdByOwnerId(userId);
        List<FarmerSelectDTO> list = farmerMapper.selectByStoreId(storeId);
        return Result.success(list);
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
}