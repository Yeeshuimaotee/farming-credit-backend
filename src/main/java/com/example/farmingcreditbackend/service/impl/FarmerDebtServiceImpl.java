package com.example.farmingcreditbackend.service.impl;

import com.example.farmingcreditbackend.dto.FarmerDebtDTO;
import com.example.farmingcreditbackend.entity.Farmer;
import com.example.farmingcreditbackend.entity.Order;
import com.example.farmingcreditbackend.mapper.FarmerMapper;
import com.example.farmingcreditbackend.mapper.OrderMapper;
import com.example.farmingcreditbackend.service.FarmerDebtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 农户欠款服务实现类
 */
@Service
public class FarmerDebtServiceImpl implements FarmerDebtService {
    
    @Autowired
    private FarmerMapper farmerMapper;
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Override
    public FarmerDebtDTO getFarmerDebtOverview(Long farmerId) {
        Farmer farmer = farmerMapper.selectById(farmerId);
        if (farmer == null) {
            throw new RuntimeException("农户不存在");
        }
        
        FarmerDebtDTO dto = new FarmerDebtDTO();
        dto.setFarmerId(farmer.getId());
        dto.setFarmerName(farmer.getFarmerName());
        dto.setPhone(farmer.getPhone());
        dto.setCreditScore(farmer.getCreditScore());
        dto.setCreditLevel(farmer.getCreditLevel());
        
        List<Order> allOrders = orderMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Order>()
                .eq("farmer_id", farmerId));
        
        int totalOrderCount = allOrders.size();
        dto.setTotalOrderCount(totalOrderCount);
        
        List<Order> unpaidOrders = allOrders.stream()
                .filter(o -> !"PAID_OFF".equals(o.getOrderStatus()))
                .collect(Collectors.toList());
        
        int unpaidOrderCount = unpaidOrders.size();
        dto.setUnpaidOrderCount(unpaidOrderCount);
        
        BigDecimal totalDebt = unpaidOrders.stream()
                .map(Order::getDebtAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotalDebt(totalDebt);
        
        List<Order> overdueOrders = unpaidOrders.stream()
                .filter(o -> "OVERDUE".equals(o.getOrderStatus()) || 
                        (o.getOverdueDays() != null && o.getOverdueDays() > 0))
                .collect(Collectors.toList());
        
        int overdueOrderCount = overdueOrders.size();
        dto.setOverdueOrderCount(overdueOrderCount);
        
        BigDecimal overdueDebt = overdueOrders.stream()
                .map(Order::getDebtAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setOverdueDebt(overdueDebt);
        
        BigDecimal normalDebt = totalDebt.subtract(overdueDebt);
        dto.setNormalDebt(normalDebt);
        
        // 最近订单：查询所有订单（包括已还清），按订单日期降序排列（最新的在前），取前 5 条
        // 注意：处理 orderDate 为 null 的情况，使用 id 作为次要排序条件
        List<FarmerDebtDTO.DebtItem> upcomingRepayments = allOrders.stream()
                .sorted((o1, o2) -> {
                    // 首先比较订单日期（降序：最新的在前）
                    if (o1.getOrderDate() == null && o2.getOrderDate() == null) {
                        return 0;
                    }
                    if (o1.getOrderDate() == null) {
                        return 1; // null 排在最后
                    }
                    if (o2.getOrderDate() == null) {
                        return -1; // null 排在最后
                    }
                    int dateCompare = o2.getOrderDate().compareTo(o1.getOrderDate());
                    if (dateCompare != 0) {
                        return dateCompare;
                    }
                    // 日期相同时，按 ID 降序（新创建的在前）
                    return o2.getId().compareTo(o1.getId());
                })
                .limit(5)
                .map(order -> {
                    FarmerDebtDTO.DebtItem item = new FarmerDebtDTO.DebtItem();
                    item.setOrderId(order.getId());
                    item.setOrderNo(order.getOrderNo());
                    item.setOrderDate(order.getOrderDate() != null ? 
                            order.getOrderDate().toString() : null);
                    item.setTotalAmount(order.getTotalAmount());
                    item.setPaidAmount(order.getPaidAmount());
                    item.setDebtAmount(order.getDebtAmount());
                    item.setDueDate(order.getDueDate().toString());
                    item.setOverdueDays(order.getOverdueDays() != null ? 
                            order.getOverdueDays() : 0);
                    item.setOrderStatus(order.getOrderStatus());
                    return item;
                })
                .collect(Collectors.toList());
        
        dto.setUpcomingRepayments(upcomingRepayments);
        
        List<FarmerDebtDTO.DebtTrend> debtTrend = calculateDebtTrend(farmerId, 6);
        dto.setDebtTrend(debtTrend);
        
        return dto;
    }
    
    private List<FarmerDebtDTO.DebtTrend> calculateDebtTrend(Long farmerId, Integer months) {
        List<Order> allOrders = orderMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Order>()
                .eq("farmer_id", farmerId));
        
        Map<String, BigDecimal> monthlyDebt = new LinkedHashMap<>();
        YearMonth currentMonth = YearMonth.now();
        
        for (int i = months - 1; i >= 0; i--) {
            YearMonth month = currentMonth.minusMonths(i);
            String monthKey = month.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            monthlyDebt.put(monthKey, BigDecimal.ZERO);
        }
        
        allOrders.forEach(order -> {
            if (order.getOrderDate() != null) {
                YearMonth orderMonth = YearMonth.from(order.getOrderDate());
                String monthKey = orderMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
                
                if (monthlyDebt.containsKey(monthKey)) {
                    BigDecimal existingDebt = monthlyDebt.get(monthKey);
                    BigDecimal newDebt = existingDebt.add(
                            order.getDebtAmount() != null ? order.getDebtAmount() : BigDecimal.ZERO);
                    monthlyDebt.put(monthKey, newDebt);
                }
            }
        });
        
        return monthlyDebt.entrySet().stream()
                .map(entry -> {
                    FarmerDebtDTO.DebtTrend trend = new FarmerDebtDTO.DebtTrend();
                    trend.setMonth(entry.getKey());
                    trend.setDebtAmount(entry.getValue());
                    return trend;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public Object getDebtTrend(Long farmerId, Integer months) {
        return calculateDebtTrend(farmerId, months);
    }
}
