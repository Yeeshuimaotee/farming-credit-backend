package com.example.farmingcreditbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.farmingcreditbackend.dto.OrderListDTO;
import com.example.farmingcreditbackend.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 分页查询订单列表（关联农户表获取农户名称）
     */
    @Select("SELECT co.order_no, f.farmer_name, co.order_date, co.total_amount, co.debt_amount, co.due_date, co.order_status " +
            "FROM credit_order co " +
            "LEFT JOIN farmer f ON co.farmer_id = f.id " +
            "WHERE co.store_id = #{storeId} " +
            "AND (#{farmerName} IS NULL OR f.farmer_name LIKE CONCAT('%', #{farmerName}, '%')) " +
            "AND (#{status} IS NULL OR co.order_status = #{status}) " +
            "ORDER BY co.create_time DESC")
    Page<OrderListDTO> selectOrderListPage(Page<?> page,
                                          @Param("storeId") Long storeId,
                                          @Param("farmerName") String farmerName,
                                          @Param("status") String status);
}