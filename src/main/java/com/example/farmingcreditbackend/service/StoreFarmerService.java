package com.example.farmingcreditbackend.service;

import com.example.farmingcreditbackend.dto.FarmerCreditInfoDTO;
import com.example.farmingcreditbackend.entity.FarmerStoreRel;
import com.example.farmingcreditbackend.exception.BusinessException;
import com.example.farmingcreditbackend.mapper.FarmerStoreRelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class StoreFarmerService {

    private final FarmerStoreRelMapper farmerStoreRelMapper;

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
}