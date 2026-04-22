package com.example.farmingcreditbackend.service;

import com.example.farmingcreditbackend.dto.FarmerDebtDTO;

/**
 * 农户服务接口
 */
public interface FarmerDebtService {
    
    /**
     * 获取农户欠款总览
     */
    FarmerDebtDTO getFarmerDebtOverview(Long farmerId);
    
    /**
     * 获取农户欠款趋势
     */
    Object getDebtTrend(Long farmerId, Integer months);
}
