package com.example.farmingcreditbackend.controller;

import com.example.farmingcreditbackend.dto.FarmerSelectDTO;
import com.example.farmingcreditbackend.mapper.FarmerMapper;
import com.example.farmingcreditbackend.service.AuthService;
import com.example.farmingcreditbackend.service.StoreService;
import com.example.farmingcreditbackend.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/store_owner/farmers")
@RequiredArgsConstructor
public class StoreFarmerController {

    private final FarmerMapper farmerMapper;
    private final StoreService storeService;
    private final AuthService authService;

    @GetMapping("/select")
    public Result<List<FarmerSelectDTO>> getFarmerSelect() {
        Long userId = authService.getCurrentUser().getId();
        Long storeId = storeService.getStoreIdByOwnerId(userId);
        List<FarmerSelectDTO> list = farmerMapper.selectByStoreId(storeId);
        return Result.success(list);
    }
}