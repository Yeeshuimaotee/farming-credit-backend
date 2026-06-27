package com.example.farmingcreditbackend.service.impl;

import com.example.farmingcreditbackend.entity.CreditEvaluation;
import com.example.farmingcreditbackend.entity.Farmer;
import com.example.farmingcreditbackend.entity.Order;
import com.example.farmingcreditbackend.entity.Repayment;
import com.example.farmingcreditbackend.mapper.CreditEvaluationMapper;
import com.example.farmingcreditbackend.mapper.FarmerMapper;
import com.example.farmingcreditbackend.mapper.OrderMapper;
import com.example.farmingcreditbackend.mapper.RepaymentMapper;
import com.example.farmingcreditbackend.service.CreditEvaluationService;
import com.example.farmingcreditbackend.service.CreditEvaluationWeightService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 信用评估服务实现类
 */
@Slf4j
@Service
public class CreditEvaluationServiceImpl implements CreditEvaluationService {
    
    @Autowired
    private CreditEvaluationMapper creditEvaluationMapper;
    
    @Autowired
    private FarmerMapper farmerMapper;
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private RepaymentMapper repaymentMapper;
    
    @Autowired
    private CreditEvaluationWeightService weightService;
    
    @Override
    @Transactional
    public CreditEvaluation evaluateFarmer(Long farmerId, Long storeId, String evaluationType, Long evaluatorId, String evaluatorName) {
        Farmer farmer = farmerMapper.selectById(farmerId);
        if (farmer == null) {
            throw new RuntimeException("农户不存在");
        }
        
        List<Order> orders = orderMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Order>()
                .eq("farmer_id", farmerId)
                .eq("store_id", storeId));
        
        List<Repayment> repayments = repaymentMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Repayment>()
                .eq("farmer_id", farmerId)
                .eq("store_id", storeId));
        
        CreditEvaluation evaluation = new CreditEvaluation();
        evaluation.setFarmerId(farmerId);
        evaluation.setStoreId(storeId);
        evaluation.setEvaluationDate(LocalDate.now());
        evaluation.setEvaluationType(evaluationType);
        evaluation.setEvaluatorId(evaluatorId);
        evaluation.setEvaluatorName(evaluatorName);
        
        int totalOrderCount = orders.size();
        evaluation.setTotalOrderCount(totalOrderCount);
        
        BigDecimal totalAmount = orders.stream()
                .map(Order::getTotalAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        evaluation.setTotalTransactionAmount(totalAmount);
        
        long onTimeCount = repayments.stream()
                .filter(r -> r.getIsOverdue() == 0 || r.getOverdueDays() == null || r.getOverdueDays() == 0)
                .count();
        
        BigDecimal repaymentRate = totalOrderCount > 0 
                ? BigDecimal.valueOf(onTimeCount).multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalOrderCount), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        evaluation.setRepaymentRate(repaymentRate);
        
        BigDecimal avgOverdueDays = repayments.stream()
                .filter(r -> r.getOverdueDays() != null && r.getOverdueDays() > 0)
                .map(Repayment::getOverdueDays)
                .map(BigDecimal::valueOf)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        long overdueRepayments = repayments.stream()
                .filter(r -> r.getOverdueDays() != null && r.getOverdueDays() > 0)
                .count();
        
        avgOverdueDays = overdueRepayments > 0 
                ? avgOverdueDays.divide(BigDecimal.valueOf(overdueRepayments), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        evaluation.setAvgOverdueDays(avgOverdueDays);
        
        int totalOverdueCount = (int) repayments.stream()
                .filter(r -> r.getIsOverdue() == 1 || (r.getOverdueDays() != null && r.getOverdueDays() > 0))
                .count();
        evaluation.setTotalOverdueCount(totalOverdueCount);
        
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        List<Order> recentOrders = orders.stream()
                .filter(o -> o.getCreateTime() != null && o.getCreateTime().isAfter(oneYearAgo))
                .collect(Collectors.toList());
        evaluation.setCreditFrequency(recentOrders.size());
        
        BigDecimal annualCreditAmount = recentOrders.stream()
                .map(Order::getTotalAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        evaluation.setAnnualCreditAmount(annualCreditAmount);
        
        long activeMonths = orders.stream()
                .map(Order::getOrderDate)
                .filter(date -> date != null)
                .distinct()
                .map(date -> date.getMonthValue())
                .distinct()
                .count();
        BigDecimal activeMonthRatio = BigDecimal.valueOf(activeMonths)
                .divide(BigDecimal.valueOf(12), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        evaluation.setActiveMonthRatio(activeMonthRatio);
        
        // 获取信用评估权重配置
        com.example.farmingcreditbackend.entity.CreditEvaluationWeight weightConfig = weightService.getWeightByStoreId(storeId);
        int baseScore = weightConfig.getBaseScore();
        int score = baseScore;
        
        // 使用配置的权重计算评分
        score += (int) (repaymentRate.doubleValue() * weightConfig.getRepaymentRateWeight());
        
        if (avgOverdueDays.compareTo(BigDecimal.ZERO) > 0) {
            score -= (int) (avgOverdueDays.intValue() * weightConfig.getOverdueDaysWeight());
        }
        
        score -= (int) (totalOverdueCount * weightConfig.getOverdueCountWeight());
        
        if (totalOrderCount > 10) {
            score += (int) Math.round(weightConfig.getOrderCountWeight());
        }
        
        if (annualCreditAmount.compareTo(BigDecimal.valueOf(5000)) > 0) {
            score += (int) Math.round(weightConfig.getCreditAmountWeight());
        }
        
        score = Math.max(0, Math.min(100, score));
        evaluation.setScore(score);
        
        String level;
        if (score >= 90) {
            level = "EXCELLENT";
        } else if (score >= 75) {
            level = "GOOD";
        } else if (score >= 60) {
            level = "NORMAL";
        } else if (score >= 40) {
            level = "POOR";
        } else {
            level = "BAD";
        }
        evaluation.setLevel(level);
        
        BigDecimal seasonalRiskScore = calculateSeasonalRiskScore(farmer, orders, repayments);
        evaluation.setSeasonalRiskScore(seasonalRiskScore);
        
        BigDecimal recommendedLimit = annualCreditAmount
                .divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP)
                .multiply(repaymentRate.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
        evaluation.setRecommendedCreditLimit(recommendedLimit);
        
        List<String> riskFactorsList = new ArrayList<>();
        if (totalOverdueCount > 3) {
            riskFactorsList.add("逾期次数过多");
        }
        if (avgOverdueDays.compareTo(BigDecimal.valueOf(15)) > 0) {
            riskFactorsList.add("平均逾期天数较长");
        }
        if (repaymentRate.compareTo(BigDecimal.valueOf(70)) < 0) {
            riskFactorsList.add("还款准时率较低");
        }
        if (seasonalRiskScore.compareTo(BigDecimal.valueOf(70)) > 0) {
            riskFactorsList.add("季节性风险较高");
        }
        
        if (!riskFactorsList.isEmpty()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                evaluation.setRiskFactors(objectMapper.writeValueAsString(riskFactorsList));
            } catch (Exception e) {
                log.error("转换风险因素为JSON失败：{}", e.getMessage());
                // 转换失败时使用逗号分隔的字符串作为备份
                evaluation.setRiskFactors(String.join(",", riskFactorsList));
            }
        }
        
        List<String> suggestions = new ArrayList<>();
        if (repaymentRate.compareTo(BigDecimal.valueOf(80)) < 0) {
            suggestions.add("建议加强还款提醒，提高还款准时率");
        }
        if (totalOverdueCount > 2) {
            suggestions.add("建议控制赊销额度，降低逾期风险");
        }
        if (totalOrderCount < 5) {
            suggestions.add("建议增加互动，提升客户活跃度");
        }
        
        if (!suggestions.isEmpty()) {
            evaluation.setImprovementSuggestions(String.join("；", suggestions));
        }
        
        creditEvaluationMapper.insert(evaluation);
        
        farmer.setCreditScore(score);
        farmer.setCreditLevel(level);
        farmer.setLastEvaluationTime(LocalDateTime.now());
        farmerMapper.updateById(farmer);
        
        return evaluation;
    }
    
    private BigDecimal calculateSeasonalRiskScore(Farmer farmer, List<Order> orders, List<Repayment> repayments) {
        int currentMonth = LocalDate.now().getMonthValue();
        
        boolean isPeakSeason = (currentMonth >= 2 && currentMonth <= 4) || 
                               (currentMonth >= 8 && currentMonth <= 10);
        
        BigDecimal baseRisk = BigDecimal.valueOf(50);
        
        if (isPeakSeason) {
            baseRisk = baseRisk.add(BigDecimal.valueOf(20));
        }
        
        long recentOverdue = repayments.stream()
                .filter(r -> r.getCreateTime() != null && 
                        r.getCreateTime().isAfter(LocalDateTime.now().minusMonths(6)))
                .filter(r -> r.getIsOverdue() == 1 || (r.getOverdueDays() != null && r.getOverdueDays() > 0))
                .count();
        
        baseRisk = baseRisk.add(BigDecimal.valueOf(recentOverdue * 5));
        
        BigDecimal totalDebt = farmer.getTotalDebt();
        if (totalDebt != null && totalDebt.compareTo(BigDecimal.valueOf(5000)) > 0) {
            baseRisk = baseRisk.add(BigDecimal.valueOf(15));
        }
        
        return baseRisk.min(BigDecimal.valueOf(100));
    }
    
    @Override
    public List<CreditEvaluation> getEvaluationHistory(Long farmerId) {
        return creditEvaluationMapper.selectByFarmerId(farmerId);
    }
    
    /**
     * 每月自动执行信用评估
     * 每月 1 号凌晨 2 点执行
     */
    @Scheduled(cron = "0 0 2 1 * ?")
    @Transactional
    public void autoEvaluateAllFarmers() {
        log.info("=== 开始执行月度自动信用评估 ===");
        
        try {
            // 查询所有活跃农户
            List<Farmer> farmers = farmerMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Farmer>()
                    .eq("status", 1)
            );
            
            int successCount = 0;
            int failCount = 0;
            
            // 逐个评估
            for (Farmer farmer : farmers) {
                try {
                    // 查询农户关联的店铺
                    List<Map<String, Object>> storeRels = getFarmerStoreRels(farmer.getId());
                    
                    if (storeRels.isEmpty()) {
                        log.warn("农户 {} 未关联任何店铺，跳过评估", farmer.getFarmerName());
                        continue;
                    }
                    
                    // 对每个关联的店铺进行评估
                    for (Map<String, Object> rel : storeRels) {
                        Long storeId = (Long) rel.get("store_id");
                        evaluateFarmer(farmer.getId(), storeId, "AUTO", null, "系统自动评估");
                        successCount++;
                        log.info("农户 {} 在店铺 {} 的信用评估完成", farmer.getFarmerName(), storeId);
                    }
                } catch (Exception e) {
                    log.error("农户 {} 信用评估失败：{}", farmer.getFarmerName(), e.getMessage());
                    failCount++;
                }
            }
            
            log.info("=== 月度自动信用评估完成 ===");
            log.info("成功：{}，失败：{}", successCount, failCount);
            
        } catch (Exception e) {
            log.error("月度自动信用评估异常：{}", e.getMessage(), e);
        }
    }
    
    /**
     * 获取农户与店铺的关联关系
     */
    private List<Map<String, Object>> getFarmerStoreRels(Long farmerId) {
        // 查询 farmer_store_rel 表
        String sql = "SELECT store_id FROM farmer_store_rel WHERE farmer_id = ? AND status = 1";
        try {
            return new ArrayList<>(); // 简化处理，实际应该查询数据库
        } catch (Exception e) {
            log.error("查询农户店铺关联失败：{}", e.getMessage());
            return new ArrayList<>();
        }
    }
    
    @Override
    public CreditEvaluation getLatestEvaluation(Long farmerId) {
        return creditEvaluationMapper.selectLatestByFarmerId(farmerId);
    }
    
    @Override
    public List<CreditEvaluation> getStoreEvaluations(Long storeId, int limit) {
        return creditEvaluationMapper.selectByStoreId(storeId, limit);
    }
    
    @Override
    @Transactional
    public CreditEvaluation manualEvaluate(Long farmerId, Long storeId, Long operatorId, String operatorName) {
        return evaluateFarmer(farmerId, storeId, "MANUAL", operatorId, operatorName);
    }

    @Override
    public List<Farmer> getRiskFarmers(Long storeId) {
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Farmer> wrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.in("credit_level", "POOR", "BAD")
                .eq("status", 1)
                .orderByAsc("credit_score");
        
        return farmerMapper.selectList(wrapper);
    }

    @Override
    public Object getCreditDistribution(Long storeId) {
        List<Farmer> allFarmers = farmerMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Farmer>()
                .eq("status", 1)
        );
        
        Map<String, Object> distribution = new HashMap<>();
        Map<String, Integer> levelCount = new HashMap<>();
        
        allFarmers.forEach(farmer -> {
            String level = farmer.getCreditLevel() != null ? farmer.getCreditLevel() : "UNKNOWN";
            levelCount.put(level, levelCount.getOrDefault(level, 0) + 1);
        });
        
        distribution.put("levelDistribution", levelCount);
        distribution.put("totalFarmers", allFarmers.size());
        
        // 计算平均信用评分
        int avgScore = allFarmers.stream()
                .mapToInt(f -> f.getCreditScore() != null ? f.getCreditScore() : 0)
                .sum() / (allFarmers.size() > 0 ? allFarmers.size() : 1);
        
        distribution.put("averageScore", avgScore);
        
        return distribution;
    }
}
