package com.example.farmingcreditbackend.service;

import com.example.farmingcreditbackend.entity.Farmer;
import com.example.farmingcreditbackend.exception.BusinessException;

public interface FarmerService {
    /**
     * 根据用户ID获取农户信息
     */
    Farmer getFarmerByUserId(Long userId);

    /**
     * 根据农户ID获取农户信息
     */
    Farmer getFarmerById(Long farmerId);

    /**
     * 新增农户信息
     */
    void createFarmer(Farmer farmer);

}