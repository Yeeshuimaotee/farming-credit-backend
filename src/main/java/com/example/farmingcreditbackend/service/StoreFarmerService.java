package com.example.farmingcreditbackend.service;

import com.example.farmingcreditbackend.dto.FarmerCreditDTO;
import com.example.farmingcreditbackend.dto.FarmerCreditInfoDTO;
import com.example.farmingcreditbackend.entity.Farmer;
import com.example.farmingcreditbackend.entity.FarmerStoreRel;
import com.example.farmingcreditbackend.exception.BusinessException;
import com.example.farmingcreditbackend.mapper.FarmerMapper;
import com.example.farmingcreditbackend.mapper.FarmerStoreRelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class StoreFarmerService {

    private final FarmerStoreRelMapper farmerStoreRelMapper;
    private final FarmerMapper farmerMapper;

    /**
     * 获取农户在当前店铺的信用额度信息
     */
    public FarmerCreditInfoDTO getFarmerCreditInfo(Long farmerId, Long storeId) {
        FarmerStoreRel rel = farmerStoreRelMapper.selectByFarmerAndStore(farmerId, storeId);
        if (rel == null) {
            throw new BusinessException("农户与店铺无关联");
        }

        // 处理可能的 null 值
        BigDecimal creditLimit = rel.getCreditLimit() != null ? rel.getCreditLimit() : BigDecimal.ZERO;
        BigDecimal currentDebt = rel.getCurrentDebt() != null ? rel.getCurrentDebt() : BigDecimal.ZERO;
        BigDecimal available = creditLimit.subtract(currentDebt);

        FarmerCreditInfoDTO dto = new FarmerCreditInfoDTO();
        dto.setFarmerId(farmerId);
        dto.setTotalCreditLimit(creditLimit);
        dto.setUsedCredit(currentDebt);
        dto.setAvailableCredit(available);
        return dto;
    }

    /**
     * 设置农户信用额度
     */
    @Transactional
    public void setFarmerCredit(Long farmerId, Long storeId, FarmerCreditDTO creditDTO) {
        FarmerStoreRel rel = farmerStoreRelMapper.selectByFarmerAndStore(farmerId, storeId);
        if (rel == null) {
            throw new BusinessException("农户与店铺无关联，请先建立关联关系");
        }

        // 更新关系表的信用额度
        rel.setCreditLimit(creditDTO.getCreditLimit());
        if (creditDTO.getRemark() != null && !creditDTO.getRemark().isEmpty()) {
            rel.setRemark(creditDTO.getRemark());
        }
        farmerStoreRelMapper.updateById(rel);

        // 同步更新 farmer 表的 total_credit_limit 字段！
        Farmer farmer = farmerMapper.selectById(farmerId);
        if (farmer != null) {
            farmer.setTotalCreditLimit(creditDTO.getCreditLimit());
            
            // 根据信用额度与欠款自动调整信用等级和状态
            updateCreditLevelAndStatus(farmer, rel);
            
            farmerMapper.updateById(farmer);
        }
    }
    
    /**
     * 根据信用额度与欠款自动调整信用等级和状态
     */
    private void updateCreditLevelAndStatus(Farmer farmer, FarmerStoreRel rel) {
        BigDecimal creditLimit = rel.getCreditLimit() != null ? rel.getCreditLimit() : BigDecimal.ZERO;
        BigDecimal currentDebt = rel.getCurrentDebt() != null ? rel.getCurrentDebt() : BigDecimal.ZERO;
        
        // 如果信用额度为 0，不进行调整
        if (creditLimit.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }
        
        // 计算欠款比例：欠款 / 信用额度 * 100
        BigDecimal debtRatio = currentDebt.multiply(BigDecimal.valueOf(100))
                .divide(creditLimit, 2, java.math.RoundingMode.HALF_UP);
        
        // 根据欠款比例调整信用等级
        String newCreditLevel;
        if (debtRatio.compareTo(BigDecimal.valueOf(0)) == 0) {
            // 无欠款
            newCreditLevel = "EXCELLENT";
        } else if (debtRatio.compareTo(BigDecimal.valueOf(30)) < 0) {
            // 欠款 < 30%
            newCreditLevel = "GOOD";
        } else if (debtRatio.compareTo(BigDecimal.valueOf(50)) < 0) {
            // 欠款 < 50%
            newCreditLevel = "NORMAL";
        } else if (debtRatio.compareTo(BigDecimal.valueOf(80)) < 0) {
            // 欠款 < 80%
            newCreditLevel = "POOR";
        } else {
            // 欠款 >= 80%
            newCreditLevel = "BAD";
        }
        
        // 更新信用等级
        farmer.setCreditLevel(newCreditLevel);
        
        // 根据欠款比例调整状态
        if (debtRatio.compareTo(BigDecimal.valueOf(100)) >= 0) {
            // 超额（欠款 > 信用额度），禁用农户
            farmer.setStatus(0);
        } else {
            // 正常状态
            farmer.setStatus(1);
        }
        
        // 同步更新信用评分（基于信用等级简单映射）
        Integer creditScore = calculateCreditScore(newCreditLevel);
        farmer.setCreditScore(creditScore);
    }
    
    /**
     * 根据信用等级计算对应的信用评分
     */
    private Integer calculateCreditScore(String creditLevel) {
        if (creditLevel == null) {
            return 60;
        }
        switch (creditLevel) {
            case "EXCELLENT":
                return 95;
            case "GOOD":
                return 80;
            case "NORMAL":
                return 65;
            case "POOR":
                return 45;
            case "BAD":
                return 25;
            default:
                return 60;
        }
    }
}