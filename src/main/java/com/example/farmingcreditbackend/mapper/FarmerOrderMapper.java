package com.example.farmingcreditbackend.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.farmingcreditbackend.dto.FarmerOrderDetailDTO;
import com.example.farmingcreditbackend.dto.FarmerOrderListDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface FarmerOrderMapper {

    @Select("SELECT co.id, co.order_no, co.order_date, s.store_name, co.total_amount, co.debt_amount, co.due_date, co.order_status, co.remark, co.create_time " +
            "FROM credit_order co " +
            "LEFT JOIN store s ON co.store_id = s.id " +
            "WHERE co.farmer_id = #{farmerId} " +
            "AND (#{status} IS NULL OR co.order_status = #{status}) " +
            "AND (#{startDate} IS NULL OR co.order_date >= #{startDate}) " +
            "AND (#{endDate} IS NULL OR co.order_date <= #{endDate}) " +
            "ORDER BY co.order_date DESC, co.create_time DESC")
    Page<FarmerOrderListDTO> selectFarmerOrderPage(Page<?> page,
                                                  @Param("farmerId") Long farmerId,
                                                  @Param("status") String status,
                                                  @Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);

    /**
     * 查询订单主信息（用于详情）
     */
    @Select("SELECT co.order_no, co.order_date, s.store_name, co.total_amount, co.debt_amount, co.due_date, co.order_status, co.remark, co.create_time " +
            "FROM credit_order co " +
            "LEFT JOIN store s ON co.store_id = s.id " +
            "WHERE co.id = #{orderId}")
    FarmerOrderDetailDTO selectOrderDetail(@Param("orderId") Long orderId);

    /**
     * 查询订单商品明细
     */
    @Select("SELECT product_name, specification, quantity, price, amount " +
            "FROM order_item " +
            "WHERE order_id = #{orderId}")
    List<FarmerOrderDetailDTO.OrderItemDetail> selectOrderItems(@Param("orderId") Long orderId);

    /**
     * 检查订单是否属于指定农户
     */
    @Select("SELECT COUNT(1) > 0 FROM credit_order WHERE id = #{orderId} AND farmer_id = #{farmerId}")
    boolean checkOrderBelongsToFarmer(@Param("orderId") Long orderId, @Param("farmerId") Long farmerId);
}