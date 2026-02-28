package com.example.farmingcreditbackend.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.farmingcreditbackend.dto.*;
import com.example.farmingcreditbackend.exception.BusinessException;
import com.example.farmingcreditbackend.mapper.FarmerOrderMapper;
import com.example.farmingcreditbackend.service.FarmerOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FarmerOrderServiceImpl implements FarmerOrderService {

    private final FarmerOrderMapper farmerOrderMapper;

    @Override
    public FarmerOrderListResponseDTO getFarmerOrderList(FarmerOrderListRequestDTO request, Long farmerId) {
        Page<FarmerOrderListDTO> page = new Page<>(request.getCurrentPage(), request.getSize());
        Page<FarmerOrderListDTO> resultPage = farmerOrderMapper.selectFarmerOrderPage(
                page,
                farmerId,
                request.getStatus(),
                request.getStartDate(),
                request.getEndDate()
        );
        FarmerOrderListResponseDTO response = new FarmerOrderListResponseDTO();
        response.setList(resultPage.getRecords());
        response.setTotal(resultPage.getTotal());
        return response;
    }

    @Override
    public FarmerOrderDetailDTO getFarmerOrderDetail(Long orderId, Long farmerId) {
        // 1. 查询订单主信息
        FarmerOrderDetailDTO detail = farmerOrderMapper.selectOrderDetail(orderId);
        if (detail == null) {
            throw new BusinessException("订单不存在");
        }

        // 2. 校验该订单是否属于当前农户（防止越权）
        // 注意：orderId 已关联 farmerId，但为了安全，可以通过查询订单表确认
        // 此处假设 selectOrderDetail 返回的订单信息中不包含 farmerId，需要额外查询
        // 建议在 selectOrderDetail 中增加 farmer_id 字段返回，或在查询前校验
        // 简便做法：在 Mapper 中先根据 orderId 和 farmerId 检查存在性
        boolean exists = farmerOrderMapper.checkOrderBelongsToFarmer(orderId, farmerId);
        if (!exists) {
            throw new BusinessException("无权查看此订单");
        }

        // 3. 查询商品明细
        List<FarmerOrderDetailDTO.OrderItemDetail> items = farmerOrderMapper.selectOrderItems(orderId);
        detail.setItems(items);
        System.out.println("detail"+detail);
        return detail;
    }
}