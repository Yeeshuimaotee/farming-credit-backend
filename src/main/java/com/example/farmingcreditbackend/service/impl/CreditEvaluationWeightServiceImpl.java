package com.example.farmingcreditbackend.service.impl;

import com.example.farmingcreditbackend.entity.CreditEvaluationWeight;
import com.example.farmingcreditbackend.mapper.CreditEvaluationWeightMapper;
import com.example.farmingcreditbackend.service.CreditEvaluationWeightService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 信用评估权重配置服务实现
 */
@Service
@RequiredArgsConstructor
public class CreditEvaluationWeightServiceImpl implements CreditEvaluationWeightService {

    private final CreditEvaluationWeightMapper weightMapper;

    /**
     * 获取店铺的信用评估权重配置
     * @param storeId 店铺ID
     * @return 权重配置
     */
    @Override
    public CreditEvaluationWeight getWeightByStoreId(Long storeId) {
        CreditEvaluationWeight weight = weightMapper.selectByStoreId(storeId);
        if (weight == null) {
            // 如果没有配置，返回默认配置
            weight = getDefaultWeight();
            weight.setStoreId(storeId);
            weightMapper.insert(weight);
        }
        return weight;
    }

    /**
     * 保存或更新店铺的信用评估权重配置
     * @param weight 权重配置
     * @return 保存后的权重配置
     */
    @Override
    public CreditEvaluationWeight saveOrUpdateWeight(CreditEvaluationWeight weight) {
        CreditEvaluationWeight existing = weightMapper.selectByStoreId(weight.getStoreId());
        if (existing != null) {
            weight.setId(existing.getId());
            weightMapper.updateById(weight);
        } else {
            weightMapper.insert(weight);
        }
        return weight;
    }

    /**
     * 获取默认权重配置
     * @return 默认权重配置
     */
    @Override
    public CreditEvaluationWeight getDefaultWeight() {
        CreditEvaluationWeight weight = new CreditEvaluationWeight();
        weight.setRepaymentRateWeight(0.3);
        weight.setOverdueDaysWeight(2.0);
        weight.setOverdueCountWeight(5.0);
        weight.setOrderCountWeight(5.0);
        weight.setCreditAmountWeight(5.0);
        weight.setBaseScore(60);
        return weight;
    }
}
