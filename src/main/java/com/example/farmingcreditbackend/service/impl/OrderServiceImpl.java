package com.example.farmingcreditbackend.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.farmingcreditbackend.dto.*;
import com.example.farmingcreditbackend.entity.Order;
import com.example.farmingcreditbackend.entity.OrderItem;
import com.example.farmingcreditbackend.exception.BusinessException;
import com.example.farmingcreditbackend.mapper.OrderItemMapper;
import com.example.farmingcreditbackend.mapper.OrderMapper;
import com.example.farmingcreditbackend.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final com.example.farmingcreditbackend.mapper.ProductMapper productMapper;
    private final com.example.farmingcreditbackend.mapper.FarmerStoreRelMapper farmerStoreRelMapper;
    private final com.example.farmingcreditbackend.mapper.FarmerMapper farmerMapper;

    @Override
    @Transactional
    public CreateOrderResponseDTO createCreditOrder(CreateOrderRequestDTO request, Long userId, String userName, Long storeId) {
        log.info("开始创建订单，店铺 ID: {}, 农户 ID: {}, 商品数量：{}", storeId, request.getFarmerId(), request.getItems().size());
        
        // 防重复提交检查：查询最近 5 秒内是否创建了相同的订单
        java.time.LocalDateTime fiveSecondsAgo = java.time.LocalDateTime.now().minusSeconds(5);
        List<Order> recentOrders = orderMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Order>()
                .eq("store_id", storeId)
                .eq("farmer_id", request.getFarmerId())
                .eq("order_status", "PENDING")
                .eq("total_amount", request.getItems().stream()
                    .map(item -> item.getPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())))
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add))
                .ge("create_time", fiveSecondsAgo)
                .orderByDesc("create_time")
                .last("LIMIT 1")
        );
        
        if (!recentOrders.isEmpty()) {
            log.warn("检测到重复提交订单，店铺 ID: {}, 农户 ID: {}, 最近订单 ID: {}", storeId, request.getFarmerId(), recentOrders.get(0).getId());
            Order existingOrder = recentOrders.get(0);
            CreateOrderResponseDTO response = new CreateOrderResponseDTO();
            response.setOrderId(existingOrder.getId());
            response.setOrderNo(existingOrder.getOrderNo());
            return response;
        }
        
        // 计算订单总金额
        java.math.BigDecimal totalAmount = request.getItems().stream()
                .map(item -> item.getPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())))
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        
        log.info("订单总金额：{}", totalAmount);
        
        // 创建订单主表记录
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setStoreId(storeId);
        order.setFarmerId(request.getFarmerId());
        order.setOrderDate(LocalDate.now());
        order.setDueDate(request.getDueDate());
        order.setTotalAmount(totalAmount);
        order.setPaidAmount(java.math.BigDecimal.ZERO);
        order.setDebtAmount(totalAmount);
        order.setOrderStatus("PENDING");
        order.setPaymentStatus("UNPAID");
        order.setSeasonalFlag(calculateSeasonalFlag());
        order.setRemark(request.getRemark());
        order.setCreatorId(userId);
        order.setCreatorName(userName);
        order.setCreateTime(LocalDateTime.now());
        
        int insertResult = orderMapper.insert(order);
        log.info("订单主表插入结果：{}, 订单 ID: {}", insertResult, order.getId());
        
        // 创建订单明细并扣减库存
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (CreateOrderRequestDTO.OrderItemDto itemDTO : request.getItems()) {
                OrderItem item = new OrderItem();
                item.setOrderId(order.getId());
                item.setProductId(itemDTO.getProductId());
                // 从数据库中查询商品信息来填充其他字段
                com.example.farmingcreditbackend.entity.Product product = productMapper.selectById(itemDTO.getProductId());
                if (product == null) {
                    throw new BusinessException("商品不存在，ID: " + itemDTO.getProductId());
                }
                
                // 检查库存是否充足
                if (product.getStock() == null || product.getStock() < itemDTO.getQuantity()) {
                    throw new BusinessException("商品库存不足：" + product.getProductName() + 
                        "，当前库存：" + product.getStock() + "，需要：" + itemDTO.getQuantity());
                }
                
                // 扣减库存
                product.setStock(product.getStock() - itemDTO.getQuantity());
                productMapper.updateById(product);
                log.info("商品库存扣减：商品 ID={}, 原库存={}, 扣减数量={}, 剩余库存={}", 
                    itemDTO.getProductId(), product.getStock() + itemDTO.getQuantity(), 
                    itemDTO.getQuantity(), product.getStock());
                
                item.setProductCode(product.getProductCode());
                item.setProductName(product.getProductName());
                item.setSpecification(product.getSpecification());
                item.setUnit(product.getUnit());
                item.setPrice(itemDTO.getPrice());
                item.setQuantity(itemDTO.getQuantity());
                item.setAmount(itemDTO.getPrice().multiply(java.math.BigDecimal.valueOf(itemDTO.getQuantity())));
                item.setCreateTime(LocalDateTime.now());
                int itemInsertResult = orderItemMapper.insert(item);
                log.info("订单明细插入结果：{}, 商品 ID: {}", itemInsertResult, itemDTO.getProductId());
            }
        }
        
        // 订单创建成功后，更新农户的欠款
        updateFarmerDebtAfterOrderCreated(request.getFarmerId(), storeId, totalAmount);
        
        CreateOrderResponseDTO response = new CreateOrderResponseDTO();
        response.setOrderId(order.getId());
        response.setOrderNo(order.getOrderNo());
        
        log.info("订单创建成功，订单号：{}, 订单 ID: {}", order.getOrderNo(), order.getId());
        return response;
    }
    
    /**
     * 订单创建后更新农户的欠款记录
     */
    private void updateFarmerDebtAfterOrderCreated(Long farmerId, Long storeId, BigDecimal orderAmount) {
        try {
            log.info("订单创建后更新农户欠款记录：农户 ID={}, 店铺 ID={}, 订单金额={}", farmerId, storeId, orderAmount);
            
            // 1. 更新 farmer_store_rel 表的欠款
            com.example.farmingcreditbackend.entity.FarmerStoreRel rel = 
                farmerStoreRelMapper.selectByFarmerAndStore(farmerId, storeId);
            
            if (rel != null) {
                BigDecimal currentDebt = rel.getCurrentDebt() != null ? rel.getCurrentDebt() : BigDecimal.ZERO;
                BigDecimal newDebt = currentDebt.add(orderAmount);
                
                rel.setCurrentDebt(newDebt);
                farmerStoreRelMapper.updateById(rel);
                
                log.info("farmer_store_rel 欠款更新成功：原欠款={}, 订单金额={}, 新欠款={}", 
                    currentDebt, orderAmount, newDebt);
                
                // 2. 更新 farmer 表的 total_debt
                com.example.farmingcreditbackend.entity.Farmer farmer = farmerMapper.selectById(farmerId);
                if (farmer != null) {
                    BigDecimal farmerTotalDebt = farmer.getTotalDebt() != null ? farmer.getTotalDebt() : BigDecimal.ZERO;
                    BigDecimal newFarmerDebt = farmerTotalDebt.add(orderAmount);
                    
                    farmer.setTotalDebt(newFarmerDebt);
                    
                    // 根据信用额度和欠款自动调整信用等级和状态
                    updateCreditLevelAndStatus(farmer, rel);
                    
                    farmerMapper.updateById(farmer);
                    
                    log.info("farmer 表总欠款和信用等级更新成功：原总欠款={}, 订单金额={}, 新总欠款={}", 
                        farmerTotalDebt, orderAmount, newFarmerDebt);
                }
            }
        } catch (Exception e) {
            log.error("更新农户欠款记录失败：农户 ID={}, 错误={}", farmerId, e.getMessage());
            // 这里不抛出异常，避免影响订单创建流程
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

    @Override
    public OrderListResponseDTO getOrderList(OrderListRequestDTO request, Long storeId) {
        Page<OrderListDTO> page = new Page<>(request.getCurrentPage(), request.getSize());
        Page<OrderListDTO> resultPage = orderMapper.selectOrderListPage(page, storeId, request.getFarmerName(), request.getStatus());
        OrderListResponseDTO response = new OrderListResponseDTO();
        response.setList(resultPage.getRecords());
        response.setTotal(resultPage.getTotal());
        return response;
    }

    @Override
    public OrderResponseDTO getOrderDetail(Long orderId, Long storeId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getStoreId().equals(storeId)) {
            throw new BusinessException("订单不存在");
        }
        
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setOrderNo(order.getOrderNo());
        dto.setStoreId(order.getStoreId());
        dto.setFarmerId(order.getFarmerId());
        dto.setOrderDate(order.getOrderDate());
        dto.setDueDate(order.getDueDate());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setPaidAmount(order.getPaidAmount());
        dto.setDebtAmount(order.getDebtAmount());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setOverdueDays(order.getOverdueDays());
        dto.setSeasonalFlag(order.getSeasonalFlag());
        dto.setRemark(order.getRemark());
        dto.setCreatorName(order.getCreatorName());
        dto.setApproverName(order.getApproverName());
        dto.setApproveTime(order.getApproveTime());
        
        // 查询订单明细
        List<OrderItem> items = orderItemMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<OrderItem>()
                .eq("order_id", orderId)
        );
        dto.setItems(items);
        
        return dto;
    }

    @Override
    @Transactional
    public void approveOrder(Long orderId, Long userId, String userName) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 幂等性：如果订单已经是审核通过状态，直接返回成功
        if ("APPROVED".equals(order.getOrderStatus())) {
            log.info("订单 {} 已被审核通过（可能是重复请求），直接返回成功", orderId);
            return;
        }

        // 如果订单已拒绝或已取消，不允许再审核
        if ("REJECTED".equals(order.getOrderStatus()) || "CANCELLED".equals(order.getOrderStatus())) {
            throw new BusinessException("订单状态为" + order.getOrderStatus() + "，无法审核");
        }

        // 只有待审核状态的订单才能审核
        if (!"PENDING".equals(order.getOrderStatus())) {
            throw new BusinessException("只有待审核状态的订单才能审核");
        }

        order.setOrderStatus("APPROVED");
        order.setApproverId(userId);
        order.setApproverName(userName);
        order.setApproveTime(LocalDateTime.now());

        orderMapper.updateById(order);
        log.info("订单 {} 审核通过，操作人：{}", orderId, userName);
    }

    @Override
    @Transactional
    public void rejectOrder(Long orderId, Long userId, String userName, String reason) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 幂等性：如果订单已经被拒绝，直接返回成功
        if ("REJECTED".equals(order.getOrderStatus())) {
            log.info("订单 {} 已被拒绝（可能是重复请求），直接返回成功", orderId);
            return;
        }

        // 如果订单已审核通过或已取消，不允许再拒绝
        if ("APPROVED".equals(order.getOrderStatus()) || "CANCELLED".equals(order.getOrderStatus())) {
            throw new BusinessException("订单状态为" + order.getOrderStatus() + "，无法拒绝");
        }

        // 只有待审核状态的订单才能拒绝
        if (!"PENDING".equals(order.getOrderStatus())) {
            throw new BusinessException("只有待审核状态的订单才能拒绝");
        }

        order.setOrderStatus("REJECTED");
        order.setApproverId(userId);
        order.setApproverName(userName);
        order.setApproveTime(LocalDateTime.now());
        order.setCancelReason(reason);
        
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId, Long userId, String userName, String reason) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        if ("PAID_OFF".equals(order.getOrderStatus()) || "CANCELLED".equals(order.getOrderStatus())) {
            throw new BusinessException("已还清或已取消的订单不能取消");
        }
        
        order.setOrderStatus("CANCELLED");
        order.setCancelReason(reason);
        order.setCancellerId(userId);
        order.setCancellerName(userName);
        order.setCancelTime(LocalDateTime.now());
        
        orderMapper.updateById(order);
    }

    /**
     * 生成订单编号：CO+ 年月日 +6 位序列
     */
    private String generateOrderNo() {
        String dateStr = LocalDate.now().toString().replace("-", "");
        String prefix = "CO" + dateStr;
        
        Order lastOrder = orderMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Order>()
                .likeRight("order_no", prefix)
                .orderByDesc("order_no")
                .last("LIMIT 1")
        ).stream().findFirst().orElse(null);
        
        int sequence = 1;
        if (lastOrder != null && lastOrder.getOrderNo() != null) {
            String lastSeq = lastOrder.getOrderNo().substring(prefix.length());
            sequence = Integer.parseInt(lastSeq) + 1;
        }
        
        return prefix + String.format("%06d", sequence);
    }

    /**
     * 计算季节性标记：0-非季节性，1-春耕，2-夏管，3-秋收，4-冬储
     */
    private int calculateSeasonalFlag() {
        int month = LocalDate.now().getMonthValue();
        if (month >= 2 && month <= 4) {
            return 1; // 春耕
        } else if (month >= 5 && month <= 7) {
            return 2; // 夏管
        } else if (month >= 8 && month <= 10) {
            return 3; // 秋收
        } else {
            return 4; // 冬储
        }
    }
}