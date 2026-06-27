package com.example.farmingcreditbackend.service;

import com.example.farmingcreditbackend.entity.CreditEvaluationWeight;

/**
 * 信用评估权重配置服务接口
 */
public interface CreditEvaluationWeightService {
    
    /**
     * 获取店铺的信用评估权重配置
     * @param storeId 店铺ID
     * @return 权重配置
     */
    CreditEvaluationWeight getWeightByStoreId(Long storeId);
    
    /**
     * 保存或更新店铺的信用评估权重配置
     * @param weight 权重配置
     * @return 保存后的权重配置
     */
    CreditEvaluationWeight saveOrUpdateWeight(CreditEvaluationWeight weight);
    
    /**
     * 获取默认权重配置
     * @return 默认权重配置
     */
    CreditEvaluationWeight getDefaultWeight();
}
