package com.example.farmingcreditbackend.service.impl;

import com.example.farmingcreditbackend.dto.DashboardStatisticsDTO;
import com.example.farmingcreditbackend.entity.Farmer;
import com.example.farmingcreditbackend.entity.Order;
import com.example.farmingcreditbackend.entity.OrderItem;
import com.example.farmingcreditbackend.entity.Repayment;
import com.example.farmingcreditbackend.mapper.FarmerMapper;
import com.example.farmingcreditbackend.mapper.OrderItemMapper;
import com.example.farmingcreditbackend.mapper.OrderMapper;
import com.example.farmingcreditbackend.mapper.RepaymentMapper;
import com.example.farmingcreditbackend.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据统计服务实现类
 */
@Service
public class DashboardServiceImpl implements DashboardService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private RepaymentMapper repaymentMapper;
    
    @Autowired
    private FarmerMapper farmerMapper;
    
    @Autowired
    private OrderItemMapper orderItemMapper;
    
    @Override
    public DashboardStatisticsDTO getStoreDashboard(Long storeId) {
        DashboardStatisticsDTO dto = new DashboardStatisticsDTO();
        
        dto.setSalesStatistics(calculateSalesStatistics(storeId));
        dto.setRepaymentStatistics(calculateRepaymentStatistics(storeId));
        dto.setCustomerStatistics(calculateCustomerStatistics(storeId));
        dto.setProductRanking(getHotProducts(storeId, 10));
        dto.setCustomerDebtRanking(getDebtRanking(storeId, 10));
        
        return dto;
    }
    
    private DashboardStatisticsDTO.SalesStatistics calculateSalesStatistics(Long storeId) {
        DashboardStatisticsDTO.SalesStatistics stats = new DashboardStatisticsDTO.SalesStatistics();
        
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime startOfWeek = today.minusWeeks(1).atStartOfDay();
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime startOfYear = LocalDate.of(today.getYear(), 1, 1).atStartOfDay();
        
        List<Order> allOrders = orderMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Order>()
                .eq("store_id", storeId));
        
        List<Order> todayOrders = allOrders.stream()
                .filter(o -> o.getCreateTime() != null && o.getCreateTime().isAfter(startOfDay))
                 .toList();
        
        List<Order> weekOrders = allOrders.stream()
                .filter(o -> o.getCreateTime() != null && o.getCreateTime().isAfter(startOfWeek))
                .toList();
        
        List<Order> monthOrders = allOrders.stream()
                .filter(o -> o.getCreateTime() != null && o.getCreateTime().isAfter(startOfMonth))
                .toList();
        
        List<Order> yearOrders = allOrders.stream()
                .filter(o -> o.getCreateTime() != null && o.getCreateTime().isAfter(startOfYear))
                .toList();
        
        BigDecimal todaySales = todayOrders.stream()
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal weekSales = weekOrders.stream()
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal monthSales = monthOrders.stream()
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal yearSales = yearOrders.stream()
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        long creditOrders = allOrders.stream()
                .filter(o -> "CREDIT".equals(o.getInvoiceType()) || o.getDebtAmount() != null && o.getDebtAmount().compareTo(BigDecimal.ZERO) > 0)
                .count();
        
        BigDecimal creditSalesRatio = allOrders.size() > 0
                ? BigDecimal.valueOf(creditOrders).multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(allOrders.size()), 2, BigDecimal.ROUND_HALF_UP)
                : BigDecimal.ZERO;
        
        stats.setTodaySales(todaySales);
        stats.setWeekSales(weekSales);
        stats.setMonthSales(monthSales);
        stats.setYearSales(yearSales);
        stats.setCreditSalesRatio(creditSalesRatio);
        stats.setTodayOrderCount(todayOrders.size());
        stats.setMonthOrderCount(monthOrders.size());
        
        return stats;
    }
    
    private DashboardStatisticsDTO.RepaymentStatistics calculateRepaymentStatistics(Long storeId) {
        DashboardStatisticsDTO.RepaymentStatistics stats = new DashboardStatisticsDTO.RepaymentStatistics();
        
        List<Repayment> allRepayments = repaymentMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Repayment>()
                .eq("store_id", storeId));
        
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
        
        List<Repayment> monthRepayments = allRepayments.stream()
                .filter(r -> r.getCreateTime() != null && r.getCreateTime().isAfter(startOfMonth))
                .toList();
        
        BigDecimal totalRepayment = allRepayments.stream()
                .map(Repayment::getRepaymentAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal monthRepayment = monthRepayments.stream()
                .map(Repayment::getRepaymentAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        List<Order> allOrders = orderMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Order>()
                .eq("store_id", storeId));
        
        long totalOrders = allOrders.size();
        long paidOrders = allOrders.stream()
                .filter(o -> "PAID_OFF".equals(o.getOrderStatus()) || "PAID".equals(o.getPaymentStatus()))
                .count();
        
        BigDecimal repaymentRate = totalOrders > 0
                ? BigDecimal.valueOf(paidOrders).multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalOrders), 2, BigDecimal.ROUND_HALF_UP)
                : BigDecimal.ZERO;
        
        long overdueOrders = allOrders.stream()
                .filter(o -> "OVERDUE".equals(o.getOrderStatus()) || (o.getOverdueDays() != null && o.getOverdueDays() > 0))
                .count();
        
        BigDecimal overdueRate = totalOrders > 0
                ? BigDecimal.valueOf(overdueOrders).multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalOrders), 2, BigDecimal.ROUND_HALF_UP)
                : BigDecimal.ZERO;
        
        BigDecimal totalDebt = allOrders.stream()
                .map(Order::getDebtAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal overdueDebt = allOrders.stream()
                .filter(o -> "OVERDUE".equals(o.getOrderStatus()) || (o.getOverdueDays() != null && o.getOverdueDays() > 0))
                .map(Order::getDebtAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        stats.setTotalRepayment(totalRepayment);
        stats.setMonthRepayment(monthRepayment);
        stats.setRepaymentRate(repaymentRate);
        stats.setOverdueRate(overdueRate);
        stats.setTotalDebt(totalDebt);
        stats.setOverdueDebt(overdueDebt);
        
        return stats;
    }
    
    private DashboardStatisticsDTO.CustomerStatistics calculateCustomerStatistics(Long storeId) {
        DashboardStatisticsDTO.CustomerStatistics stats = new DashboardStatisticsDTO.CustomerStatistics();
        
        List<Farmer> allFarmers = farmerMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Farmer>());
        
        List<Farmer> activeFarmers = allFarmers.stream()
                .filter(f -> f.getStatus() != null && f.getStatus() == 1)
                .toList();
        
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
        
        List<Order> monthOrders = orderMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Order>()
                .eq("store_id", storeId)
                .ge("create_time", startOfMonth));
        
        Set<Long> activeFarmerIds = monthOrders.stream()
                .map(Order::getFarmerId)
                .collect(Collectors.toSet());
        
        List<Farmer> newFarmers = allFarmers.stream()
                .filter(f -> f.getCreateTime() != null && f.getCreateTime().isAfter(startOfMonth))
                .toList();
        
        BigDecimal avgCreditScore = activeFarmers.stream()
                .map(Farmer::getCreditScore)
                .filter(Objects::nonNull)
                .map(BigDecimal::valueOf)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(activeFarmers.size()), 2, BigDecimal.ROUND_HALF_UP);
        
        Map<String, Integer> creditLevelDist = new HashMap<>();
        activeFarmers.forEach(f -> {
            String level = f.getCreditLevel() != null ? f.getCreditLevel() : "UNKNOWN";
            creditLevelDist.put(level, creditLevelDist.getOrDefault(level, 0) + 1);
        });
        
        stats.setTotalCustomerCount(allFarmers.size());
        stats.setNewCustomerCount(newFarmers.size());
        stats.setActiveCustomerCount(activeFarmerIds.size());
        stats.setAvgCreditScore(avgCreditScore);
        stats.setCreditLevelDistribution(creditLevelDist);
        
        return stats;
    }
    
    @Override
    public List<DashboardStatisticsDTO.ProductRanking> getHotProducts(Long storeId, int limit) {
        List<Order> orders = orderMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Order>()
                .eq("store_id", storeId)
                .orderByDesc("create_time")
                .last("LIMIT 100"));
        
        List<Long> orderIds = orders.stream().map(Order::getId).toList();
        
        if (orderIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<OrderItem> orderItems = orderItemMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<OrderItem>()
                .in("order_id", orderIds));
        
        Map<Long, DashboardStatisticsDTO.ProductRanking> productStats = new HashMap<>();
        
        orderItems.forEach(item -> {
            DashboardStatisticsDTO.ProductRanking ranking = productStats.computeIfAbsent(item.getProductId(), k -> {
                DashboardStatisticsDTO.ProductRanking r = new DashboardStatisticsDTO.ProductRanking();
                r.setProductId(item.getProductId());
                r.setProductName(item.getProductName());
                r.setSalesCount(0);
                r.setSalesAmount(BigDecimal.ZERO);
                return r;
            });
            
            ranking.setSalesCount(ranking.getSalesCount() + item.getQuantity());
            ranking.setSalesAmount(ranking.getSalesAmount().add(item.getAmount()));
        });
        
        return productStats.values().stream()
                .sorted(Comparator.comparing(DashboardStatisticsDTO.ProductRanking::getSalesAmount).reversed())
                .limit(limit)
                .toList();
    }
    
    @Override
    public List<DashboardStatisticsDTO.CustomerDebtRanking> getDebtRanking(Long storeId, int limit) {
        List<Order> orders = orderMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Order>()
                .eq("store_id", storeId)
                .gt("debt_amount", 0)
                .orderByDesc("debt_amount"));
        
        Map<Long, DashboardStatisticsDTO.CustomerDebtRanking> farmerDebtMap = new HashMap<>();
        
        orders.forEach(order -> {
            DashboardStatisticsDTO.CustomerDebtRanking ranking = farmerDebtMap.computeIfAbsent(order.getFarmerId(), k -> {
                DashboardStatisticsDTO.CustomerDebtRanking r = new DashboardStatisticsDTO.CustomerDebtRanking();
                r.setFarmerId(order.getFarmerId());
                r.setFarmerName("农户" + order.getFarmerId());
                r.setDebtAmount(BigDecimal.ZERO);
                r.setOverdueDays(0);
                return r;
            });
            
            ranking.setDebtAmount(ranking.getDebtAmount().add(order.getDebtAmount()));
            if (order.getOverdueDays() != null && order.getOverdueDays() > ranking.getOverdueDays()) {
                ranking.setOverdueDays(order.getOverdueDays());
            }
        });
        
        return farmerDebtMap.values().stream()
                .sorted(Comparator.comparing(DashboardStatisticsDTO.CustomerDebtRanking::getDebtAmount).reversed())
                .limit(limit)
                .toList();
    }
    
    @Override
    public Object getSalesTrend(Long storeId, String startDate, String endDate, String type) {
        List<Order> orders = orderMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Order>()
                .eq("store_id", storeId)
                .between("order_date", startDate, endDate));
        
        Map<String, BigDecimal> trendMap = new LinkedHashMap<>();
        
        orders.forEach(order -> {
            String key;
            if ("day".equals(type)) {
                key = order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } else if ("month".equals(type)) {
                key = order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            } else {
                key = order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
            
            trendMap.put(key, trendMap.getOrDefault(key, BigDecimal.ZERO).add(
                    order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO));
        });
        
        return trendMap;
    }
    
    @Override
    public Object getAdminDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        
        // 用户统计
        List<Farmer> allFarmers = farmerMapper.selectList(null);
        dashboard.put("totalUsers", allFarmers.size());
        dashboard.put("activeUsers", allFarmers.stream().filter(f -> f.getStatus() == 1).count());
        
        // 订单统计
        List<Order> allOrders = orderMapper.selectList(null);
        dashboard.put("totalOrders", allOrders.size());
        dashboard.put("pendingOrders", allOrders.stream().filter(o -> "PENDING".equals(o.getOrderStatus())).count());
        dashboard.put("overdueOrders", allOrders.stream().filter(o -> o.getOverdueDays() != null && o.getOverdueDays() > 0).count());
        
        // 销售额统计
        BigDecimal totalSales = allOrders.stream()
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal todaySales = allOrders.stream()
                .filter(o -> o.getCreateTime() != null && o.getCreateTime().isAfter(LocalDate.now().atStartOfDay()))
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        dashboard.put("totalSales", totalSales);
        dashboard.put("todaySales", todaySales);
        
        return dashboard;
    }
    
    @Override
    public Object getSystemLogs(Integer page, Integer size) {
        // TODO: 实现系统日志查询
        Map<String, Object> result = new HashMap<>();
        result.put("records", new ArrayList<>());
        result.put("total", 0);
        return result;
    }
    
    @Override
    public Object getStoreOwnerStatistics(Long storeId, String startDate, String endDate) {
        Map<String, Object> statistics = new HashMap<>();
        
        // 销售统计
        List<Order> orders = orderMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Order>()
                .eq("store_id", storeId));
        
        if (startDate != null && endDate != null) {
            orders = orders.stream()
                    .filter(o -> o.getOrderDate() != null && 
                            o.getOrderDate().compareTo(java.time.LocalDate.parse(startDate)) >= 0 &&
                            o.getOrderDate().compareTo(java.time.LocalDate.parse(endDate)) <= 0)
                    .collect(java.util.stream.Collectors.toList());
        }
        
        BigDecimal totalSales = orders.stream()
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        long totalOrders = orders.size();
        
        // 客户统计
        Set<Long> uniqueFarmers = orders.stream()
                .map(Order::getFarmerId)
                .filter(Objects::nonNull)
                .collect(java.util.stream.Collectors.toSet());
        
        long totalCustomers = uniqueFarmers.size();
        
        statistics.put("totalSales", totalSales);
        statistics.put("totalOrders", totalOrders);
        statistics.put("totalCustomers", totalCustomers);
        
        return statistics;
    }

    @Override
    public Object getRepaymentTrend(Long storeId, String startDate, String endDate, String type) {
        List<Repayment> repayments = repaymentMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Repayment>()
                .eq("store_id", storeId)
                .between("repayment_date", startDate, endDate));
        
        Map<String, BigDecimal> trendMap = new LinkedHashMap<>();
        
        repayments.forEach(repayment -> {
            String key;
            if ("day".equals(type)) {
                key = repayment.getRepaymentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } else if ("month".equals(type)) {
                key = repayment.getRepaymentDate().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            } else {
                key = repayment.getRepaymentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
            
            trendMap.put(key, trendMap.getOrDefault(key, BigDecimal.ZERO).add(
                    repayment.getRepaymentAmount() != null ? repayment.getRepaymentAmount() : BigDecimal.ZERO));
        });
        
        return trendMap;
    }

    @Override
    public Object getCustomerAnalysis(Long storeId) {
        Map<String, Object> analysis = new HashMap<>();
        
        // 获取所有农户
        List<Farmer> allFarmers = farmerMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Farmer>()
                .eq("status", 1));
        
        // 统计客户数量
        int totalCustomers = allFarmers.size();
        
        // 统计活跃客户（近 3 个月有订单）
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        List<Order> recentOrders = orderMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Order>()
                .eq("store_id", storeId)
                .ge("create_time", threeMonthsAgo));
        
        Set<Long> activeFarmerIds = recentOrders.stream()
                .map(Order::getFarmerId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        
        int activeCustomers = activeFarmerIds.size();
        
        // 统计新客户（近 1 个月新增）
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        int newCustomers = (int) allFarmers.stream()
                .filter(f -> f.getCreateTime() != null && f.getCreateTime().isAfter(oneMonthAgo))
                .count();
        
        // 信用等级分布
        Map<String, Integer> creditLevelDist = new HashMap<>();
        allFarmers.forEach(farmer -> {
            String level = farmer.getCreditLevel() != null ? farmer.getCreditLevel() : "UNKNOWN";
            creditLevelDist.put(level, creditLevelDist.getOrDefault(level, 0) + 1);
        });
        
        // 平均信用评分
        int avgCreditScore = allFarmers.stream()
                .mapToInt(f -> f.getCreditScore() != null ? f.getCreditScore() : 0)
                .sum() / (totalCustomers > 0 ? totalCustomers : 1);
        
        // 欠款总额统计
        BigDecimal totalDebt = allFarmers.stream()
                .map(Farmer::getTotalDebt)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        analysis.put("totalCustomers", totalCustomers);
        analysis.put("activeCustomers", activeCustomers);
        analysis.put("newCustomers", newCustomers);
        analysis.put("creditLevelDistribution", creditLevelDist);
        analysis.put("averageCreditScore", avgCreditScore);
        analysis.put("totalDebt", totalDebt);
        analysis.put("activeCustomerRate", totalCustomers > 0 
                ? BigDecimal.valueOf(activeCustomers).multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalCustomers), 2, BigDecimal.ROUND_HALF_UP)
                : BigDecimal.ZERO);
        
        return analysis;
    }
}
