package com.example.farmingcreditbackend.service;

import com.example.farmingcreditbackend.entity.CreditEvaluation;
import com.example.farmingcreditbackend.entity.Farmer;

import java.util.List;

/**
 * 信用评估服务接口
 */
public interface CreditEvaluationService {
    
    /**
     * 对农户进行信用评估
     */
    CreditEvaluation evaluateFarmer(Long farmerId, Long storeId, String evaluationType, Long evaluatorId, String evaluatorName);
    
    /**
     * 获取农户信用评估历史
     */
    List<CreditEvaluation> getEvaluationHistory(Long farmerId);
    
    /**
     * 获取农户最新评估结果
     */
    CreditEvaluation getLatestEvaluation(Long farmerId);
    
    /**
     * 获取店铺最新评估记录
     */
    List<CreditEvaluation> getStoreEvaluations(Long storeId, int limit);
    
    /**
     * 手动触发信用评估
     */
    CreditEvaluation manualEvaluate(Long farmerId, Long storeId, Long operatorId, String operatorName);
    
    /**
     * 获取风险客户列表
     */
    List<Farmer> getRiskFarmers(Long storeId);
    
    /**
     * 获取信用评分分布统计
     */
    Object getCreditDistribution(Long storeId);
}
