package com.example.farmingcreditbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.farmingcreditbackend.dto.OverdueOrderDTO;
import com.example.farmingcreditbackend.dto.RepaymentListDTO;
import com.example.farmingcreditbackend.entity.Order;
import com.example.farmingcreditbackend.entity.Repayment;

import java.time.LocalDate;
import java.util.List;

/**
 * 还款服务接口
 */
public interface RepaymentService {
    
    /**
     * 创建还款记录
     */
    Repayment createRepayment(Repayment repayment);
    
    /**
     * 根据订单 ID 查询还款记录
     */
    List<Repayment> getRepaymentsByOrderId(Long orderId);
    
    /**
     * 根据农户 ID 分页查询还款记录
     */
    Page<RepaymentListDTO> getRepaymentsByFarmerId(Long farmerId, Integer current, Integer size);
    
    /**
     * 获取还款统计信息
     */
    Object getRepaymentStatistics(Long storeId, String startDate, String endDate);
    
    /**
     * 获取逾期订单列表
     */
    List<OverdueOrderDTO> getOverdueOrders(Long storeId, Integer minOverdueDays);
    
    /**
     * 获取应收账款总览
     */
    Object getAccountsReceivable(Long storeId);
}
