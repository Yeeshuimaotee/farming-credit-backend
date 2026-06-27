package com.example.farmingcreditbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.farmingcreditbackend.dto.OverdueOrderDTO;
import com.example.farmingcreditbackend.dto.RepaymentListDTO;
import com.example.farmingcreditbackend.entity.Farmer;
import com.example.farmingcreditbackend.entity.Order;
import com.example.farmingcreditbackend.entity.ReminderLog;
import com.example.farmingcreditbackend.entity.Repayment;
import com.example.farmingcreditbackend.mapper.FarmerMapper;
import com.example.farmingcreditbackend.mapper.OrderMapper;
import com.example.farmingcreditbackend.mapper.ReminderLogMapper;
import com.example.farmingcreditbackend.mapper.RepaymentMapper;
import com.example.farmingcreditbackend.service.AuthService;
import com.example.farmingcreditbackend.service.CreditEvaluationService;
import com.example.farmingcreditbackend.service.RepaymentService;
import com.example.farmingcreditbackend.service.StoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 还款服务实现类
 */
@Slf4j
@Service
public class RepaymentServiceImpl implements RepaymentService {
    
    @Autowired
    private RepaymentMapper repaymentMapper;
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private FarmerMapper farmerMapper;
    
    @Autowired
    private ReminderLogMapper reminderLogMapper;
    
    @Autowired
    private CreditEvaluationService creditEvaluationService;
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private StoreService storeService;
    
    @Autowired
    private com.example.farmingcreditbackend.mapper.FarmerStoreRelMapper farmerStoreRelMapper;
    
    @Override
    @Transactional
    public Repayment createRepayment(Repayment repayment) {
        Order order = orderMapper.selectById(repayment.getOrderId());
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        // 设置 farmer_id 和 store_id
        repayment.setFarmerId(order.getFarmerId());
        repayment.setStoreId(order.getStoreId());
        
        BigDecimal beforeDebt = order.getDebtAmount();
        if (beforeDebt == null) {
            beforeDebt = BigDecimal.ZERO;
        }
        
        BigDecimal repaymentAmount = repayment.getRepaymentAmount();
        if (repaymentAmount == null || repaymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("还款金额必须大于 0");
        }
        
        if (repaymentAmount.compareTo(beforeDebt) > 0) {
            throw new RuntimeException("还款金额不能大于欠款金额");
        }
        
        BigDecimal afterDebt = beforeDebt.subtract(repaymentAmount);
        
        repayment.setBeforeDebt(beforeDebt);
        repayment.setAfterDebt(afterDebt);
        
        boolean isOverdue = order.getOverdueDays() != null && order.getOverdueDays() > 0;
        repayment.setIsOverdue(isOverdue ? 1 : 0);
        repayment.setOverdueDays(order.getOverdueDays() != null ? order.getOverdueDays() : 0);
        
        if (repayment.getRepaymentNo() == null || repayment.getRepaymentNo().isEmpty()) {
            String repaymentNo = "RP" + LocalDate.now().toString().replace("-", "") + 
                    String.format("%06d", repaymentMapper.selectCount(null) + 1);
            repayment.setRepaymentNo(repaymentNo);
        }
        
        if (repayment.getRepaymentDate() == null) {
            repayment.setRepaymentDate(LocalDate.now());
        }
        
        repayment.setCreateTime(LocalDateTime.now());
        
        repaymentMapper.insert(repayment);
        
        order.setPaidAmount(order.getPaidAmount() != null ? 
                order.getPaidAmount().add(repaymentAmount) : repaymentAmount);
        order.setDebtAmount(afterDebt);
        
        if (afterDebt.compareTo(BigDecimal.ZERO) <= 0) {
            order.setPaymentStatus("PAID");
            order.setOrderStatus("PAID_OFF");
            order.setActualRepayDate(LocalDate.now());
        } else {
            order.setPaymentStatus("PARTIAL");
            order.setOrderStatus("PARTIAL_PAID");
        }
        
        orderMapper.updateById(order);
        
        // 还款成功后，更新 farmer_store_rel 表和 farmer 表的欠款
        updateFarmerDebtAfterRepayment(order.getFarmerId(), order.getStoreId(), repaymentAmount);
        
        return repayment;
    }
    
    /**
     * 还款后更新农户的欠款记录
     */
    private void updateFarmerDebtAfterRepayment(Long farmerId, Long storeId, BigDecimal repaymentAmount) {
        try {
            log.info("还款后更新农户欠款记录：农户 ID={}, 店铺 ID={}, 还款金额={}", farmerId, storeId, repaymentAmount);
            
            // 1. 更新 farmer_store_rel 表的欠款
            com.example.farmingcreditbackend.entity.FarmerStoreRel rel = 
                farmerStoreRelMapper.selectByFarmerAndStore(farmerId, storeId);
            
            if (rel != null) {
                BigDecimal currentDebt = rel.getCurrentDebt() != null ? rel.getCurrentDebt() : BigDecimal.ZERO;
                BigDecimal newDebt = currentDebt.subtract(repaymentAmount);
                
                // 确保欠款不会小于 0
                if (newDebt.compareTo(BigDecimal.ZERO) < 0) {
                    newDebt = BigDecimal.ZERO;
                }
                
                rel.setCurrentDebt(newDebt);
                farmerStoreRelMapper.updateById(rel);
                
                log.info("farmer_store_rel 欠款更新成功：原欠款={}, 还款={}, 新欠款={}", 
                    currentDebt, repaymentAmount, newDebt);
                
                // 2. 更新 farmer 表的 total_debt
                com.example.farmingcreditbackend.entity.Farmer farmer = farmerMapper.selectById(farmerId);
                if (farmer != null) {
                    BigDecimal farmerTotalDebt = farmer.getTotalDebt() != null ? farmer.getTotalDebt() : BigDecimal.ZERO;
                    BigDecimal newFarmerDebt = farmerTotalDebt.subtract(repaymentAmount);
                    
                    if (newFarmerDebt.compareTo(BigDecimal.ZERO) < 0) {
                        newFarmerDebt = BigDecimal.ZERO;
                    }
                    
                    farmer.setTotalDebt(newFarmerDebt);
                    
                    // 根据信用额度和欠款自动调整信用等级和状态
                    updateCreditLevelAndStatus(farmer, rel);
                    
                    farmerMapper.updateById(farmer);
                    
                    log.info("farmer 表总欠款和信用等级更新成功：原总欠款={}, 还款={}, 新总欠款={}", 
                        farmerTotalDebt, repaymentAmount, newFarmerDebt);
                }
            }
        } catch (Exception e) {
            log.error("更新农户欠款记录失败：农户 ID={}, 错误={}", farmerId, e.getMessage());
            // 这里不抛出异常，避免影响还款流程
        }
    }
    
    /**
     * 根据信用额度与欠款自动调整信用等级和状态
     */
    private void updateCreditLevelAndStatus(
            com.example.farmingcreditbackend.entity.Farmer farmer, 
            com.example.farmingcreditbackend.entity.FarmerStoreRel rel) {
        
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
    
    /**
     * 还款成功后触发信用评估（非事务方法）
     */
    public void triggerCreditEvaluationAfterRepayment(Long farmerId, Long storeId) {
        try {
            log.info("还款成功，触发信用评估：农户 ID={}", farmerId);
            creditEvaluationService.evaluateFarmer(
                farmerId, 
                storeId, 
                "AUTO", 
                null, 
                "还款后自动评估"
            );
            log.info("信用评估完成：农户 ID={}", farmerId);
        } catch (Exception e) {
            log.error("信用评估失败：农户 ID={}, 错误={}", farmerId, e.getMessage());
            // 信用评估失败不影响还款流程
        }
    }
    
    @Override
    public List<Repayment> getRepaymentsByOrderId(Long orderId) {
        return repaymentMapper.selectList(new QueryWrapper<Repayment>()
                .eq("order_id", orderId)
                .orderByDesc("repayment_date"));
    }
    
    @Override
    public Page<RepaymentListDTO> getRepaymentsByFarmerId(Long farmerId, Integer current, Integer size) {
        Page<RepaymentListDTO> resultPage = new Page<>(current, size);
        
        // 使用自定义查询，关联订单表获取订单号
        QueryWrapper<Repayment> wrapper = new QueryWrapper<>();
        wrapper.eq("farmer_id", farmerId)
               .orderByDesc("repayment_date");
        
        Page<Repayment> repaymentPage = repaymentMapper.selectPage(new Page<>(current, size), wrapper);
        
        // 转换为 DTO，关联查询订单号
        Page<RepaymentListDTO> dtoPage = new Page<>(current, size, repaymentPage.getTotal());
        List<RepaymentListDTO> dtoList = repaymentPage.getRecords().stream()
            .map(repayment -> {
                RepaymentListDTO dto = new RepaymentListDTO();
                dto.setId(repayment.getId());
                dto.setRepaymentNo(repayment.getRepaymentNo());
                dto.setRepaymentDate(repayment.getRepaymentDate());
                dto.setRepaymentAmount(repayment.getRepaymentAmount());
                dto.setRepaymentType(repayment.getRepaymentType());
                dto.setBeforeDebt(repayment.getBeforeDebt());
                dto.setAfterDebt(repayment.getAfterDebt());
                dto.setRemark(repayment.getRemark());
                dto.setOperatorName(repayment.getOperatorName());
                
                // 关联查询订单号
                Order order = orderMapper.selectById(repayment.getOrderId());
                if (order != null) {
                    dto.setOrderNo(order.getOrderNo());
                }
                
                return dto;
            })
            .collect(Collectors.toList());
        
        dtoPage.setRecords(dtoList);
        return dtoPage;
    }
    
    @Override
    public Object getRepaymentStatistics(Long storeId, String startDate, String endDate) {
        QueryWrapper<Repayment> wrapper = new QueryWrapper<>();
        if (storeId != null) {
            wrapper.eq("store_id", storeId);
        }
        
        if (startDate != null && endDate != null) {
            wrapper.between("repayment_date", startDate, endDate);
        }
        
        List<Repayment> repayments = repaymentMapper.selectList(wrapper);
        
        BigDecimal totalAmount = repayments.stream()
                .map(Repayment::getRepaymentAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        long totalRepaymentCount = repayments.size();
        
        long overdueRepaymentCount = repayments.stream()
                .filter(r -> r.getIsOverdue() == 1)
                .count();
        
        BigDecimal overdueAmount = repayments.stream()
                .filter(r -> r.getIsOverdue() == 1)
                .map(Repayment::getRepaymentAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalAmount", totalAmount);
        statistics.put("totalRepaymentCount", totalRepaymentCount);
        statistics.put("overdueRepaymentCount", overdueRepaymentCount);
        statistics.put("overdueAmount", overdueAmount);
        statistics.put("onTimeRepaymentRate", totalRepaymentCount > 0 
                ? BigDecimal.valueOf(totalRepaymentCount - overdueRepaymentCount)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalRepaymentCount), 2, BigDecimal.ROUND_HALF_UP)
                : BigDecimal.valueOf(100));
        
        if (startDate != null && endDate != null) {
            statistics.put("startDate", startDate);
            statistics.put("endDate", endDate);
        }
        
        return statistics;
    }
    
    @Override
    public List<OverdueOrderDTO> getOverdueOrders(Long storeId, Integer minOverdueDays) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("store_id", storeId)
                .in("order_status", "APPROVED", "PARTIAL_PAID", "OVERDUE")
                .gt("debt_amount", 0);
        
        List<Order> orders = orderMapper.selectList(wrapper);
        
        // 过滤出真正逾期的订单（根据应还日期和当前日期计算）
        LocalDate today = LocalDate.now();
        List<Order> overdueOrders = orders.stream()
                .filter(order -> {
                    if (order.getDueDate() == null) return false;
                    // 计算逾期天数
                    long overdueDays = ChronoUnit.DAYS.between(order.getDueDate(), today);
                    return overdueDays > 0;
                })
                .peek(order -> {
                    // 计算并设置逾期天数
                    if (order.getDueDate() != null) {
                        long overdueDays = ChronoUnit.DAYS.between(order.getDueDate(), LocalDate.now());
                        order.setOverdueDays((int) Math.max(0, overdueDays));
                    }
                })
                .toList();
        
        // 如果指定了最小逾期天数，进一步过滤
        if (minOverdueDays != null) {
            overdueOrders = overdueOrders.stream()
                    .filter(order -> order.getOverdueDays() >= minOverdueDays)
                    .toList();
        }
        
        // 转换为 DTO 并关联农户信息和提醒状态
        return overdueOrders.stream()
                .map(order -> {
                    OverdueOrderDTO dto = new OverdueOrderDTO();
                    dto.setId(order.getId());
                    dto.setOrderNo(order.getOrderNo());
                    dto.setFarmerId(order.getFarmerId());
                    dto.setDebtAmount(order.getDebtAmount());
                    dto.setDueDate(order.getDueDate());
                    dto.setOverdueDays(order.getOverdueDays());
                    dto.setOrderStatus(order.getOrderStatus());
                    dto.setTotalAmount(order.getTotalAmount());
                    dto.setPaidAmount(order.getPaidAmount());
                    
                    // 查询农户信息
                    Farmer farmer = farmerMapper.selectById(order.getFarmerId());
                    if (farmer != null) {
                        dto.setFarmerName(farmer.getFarmerName());
                        dto.setFarmerPhone(farmer.getPhone());
                    }
                    
                    // 查询提醒状态
                    dto.setReminderStatus(getReminderStatus(order.getId(), order.getFarmerId()));
                    
                    return dto;
                })
                .sorted(Comparator.comparing(OverdueOrderDTO::getOverdueDays, Comparator.reverseOrder())
                        .thenComparing(OverdueOrderDTO::getDebtAmount, Comparator.reverseOrder()))
                .toList();
    }
    
    /**
     * 获取订单的提醒状态
     * @param orderId 订单 ID
     * @param farmerId 农户 ID
     * @return NOT_SENT-未提醒，SENT-已发出提醒，READ-已接收
     */
    private String getReminderStatus(Long orderId, Long farmerId) {
        // 先检查是否已读（通过订单号在消息内容中查找）
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return "NOT_SENT";
        }
        
        // 检查是否有已读记录
        QueryWrapper<ReminderLog> readWrapper = 
            new QueryWrapper<>();
        readWrapper.eq("farmer_id", farmerId)
                .like("message_content", order.getOrderNo())
                .isNotNull("read_time");
        
        long readCount = reminderLogMapper.selectCount(readWrapper);
        if (readCount > 0) {
            return "READ";
        }
        
        // 检查是否已发送（今天）
        QueryWrapper<ReminderLog> sentWrapper = 
            new QueryWrapper<>();
        sentWrapper.eq("farmer_id", farmerId)
                .eq("reminder_type", "OVERDUE")
                .like("message_content", order.getOrderNo())
                .ge("create_time", LocalDate.now().atStartOfDay());
        
        long sentCount = reminderLogMapper.selectCount(sentWrapper);
        if (sentCount > 0) {
            return "SENT";
        }
        
        return "NOT_SENT";
    }
    
    @Override
    public Object getAccountsReceivable(Long storeId) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("store_id", storeId)
                .in("order_status", "APPROVED", "PARTIAL_PAID", "OVERDUE")
                .gt("debt_amount", 0);
        
        List<Order> orders = orderMapper.selectList(wrapper);
        
        BigDecimal totalReceivable = orders.stream()
                .map(Order::getDebtAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal overdueReceivable = orders.stream()
                .filter(o -> o.getOverdueDays() != null && o.getOverdueDays() > 0)
                .map(Order::getDebtAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        long totalOrders = orders.size();
        long overdueOrders = orders.stream()
                .filter(o -> o.getOverdueDays() != null && o.getOverdueDays() > 0)
                .count();
        
        Map<String, Object> receivable = new HashMap<>();
        receivable.put("totalReceivable", totalReceivable);
        receivable.put("overdueReceivable", overdueReceivable);
        receivable.put("totalOrders", totalOrders);
        receivable.put("overdueOrders", overdueOrders);
        receivable.put("normalReceivable", totalReceivable.subtract(overdueReceivable));
        
        return receivable;
    }
}
