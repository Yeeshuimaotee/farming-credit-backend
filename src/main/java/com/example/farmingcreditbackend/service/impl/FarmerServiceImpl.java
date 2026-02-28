package com.example.farmingcreditbackend.service.impl;

import com.example.farmingcreditbackend.entity.Farmer;
import com.example.farmingcreditbackend.exception.BusinessException;
import com.example.farmingcreditbackend.mapper.FarmerMapper;
import com.example.farmingcreditbackend.service.FarmerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FarmerServiceImpl implements FarmerService {

    private final FarmerMapper farmerMapper;

    @Override
    public Farmer getFarmerByUserId(Long userId) {
        Farmer farmer = farmerMapper.selectByUserId(userId);
        if (farmer == null) {
            throw new BusinessException("农户信息不存在");
        }
        return farmer;
    }

    @Override
    public Farmer getFarmerById(Long farmerId) {
        Farmer farmer = farmerMapper.selectById(farmerId);
        if (farmer == null) {
            throw new BusinessException("农户信息不存在");
        }
        return farmer;
    }

    @Override
    public void createFarmer(Farmer farmer) {
        farmerMapper.insert(farmer);
    }
}