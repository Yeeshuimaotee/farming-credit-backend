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
    @Select("<script>" +
            "SELECT co.id, co.order_no, f.farmer_name, co.order_date, co.total_amount, co.debt_amount, co.due_date, co.order_status, co.create_time " +
            "FROM credit_order co " +
            "LEFT JOIN farmer f ON co.farmer_id = f.id " +
            "WHERE co.store_id = #{storeId} " +
            "<if test='farmerName != null and farmerName != \"\"'>" +
            "  AND f.farmer_name LIKE CONCAT('%', #{farmerName}, '%') " +
            "</if>" +
            "<if test='status != null and status != \"\"'>" +
            "  AND co.order_status = #{status} " +
            "</if>" +
            "ORDER BY co.create_time DESC" +
            "</script>")
    Page<OrderListDTO> selectOrderListPage(Page<?> page,
                                          @Param("storeId") Long storeId,
                                          @Param("farmerName") String farmerName,
                                          @Param("status") String status);
}