package com.example.farmingcreditbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.farmingcreditbackend.entity.Repayment;
import com.example.farmingcreditbackend.dto.OrderRepaymentRecordDTO;
import com.example.farmingcreditbackend.dto.RepaymentListDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface RepaymentMapper extends BaseMapper<Repayment> {

    /**
     * 根据订单 ID 查询还款记录，按还款日期倒序
     */
    @Select("SELECT id, repayment_no, repayment_date, repayment_amount, repayment_type, before_debt, after_debt, remark " +
            "FROM repayment " +
            "WHERE order_id = #{orderId} " +
            "ORDER BY repayment_date DESC")
    List<OrderRepaymentRecordDTO> selectByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 分页查询还款记录列表（关联订单表和农户表）
     */
    @Select("<script>" +
            "SELECT r.id, r.repayment_no, co.order_no, f.farmer_name, " +
            "       r.repayment_date, r.repayment_amount, r.repayment_type, " +
            "       r.before_debt, r.after_debt, r.remark, r.operator_name " +
            "FROM repayment r " +
            "LEFT JOIN credit_order co ON r.order_id = co.id " +
            "LEFT JOIN farmer f ON r.farmer_id = f.id " +
            "WHERE r.store_id = #{storeId} " +
            "<if test='farmerId != null'>" +
            "  AND r.farmer_id = #{farmerId} " +
            "</if>" +
            "<if test='startDate != null and startDate != \"\"'>" +
            "  AND r.repayment_date &gt;= #{startDate} " +
            "</if>" +
            "<if test='endDate != null and endDate != \"\"'>" +
            "  AND r.repayment_date &lt;= #{endDate} " +
            "</if>" +
            "ORDER BY r.repayment_date DESC, r.create_time DESC" +
            "</script>")
    Page<RepaymentListDTO> selectRepaymentListPage(Page<?> page,
                                                    @Param("storeId") Long storeId,
                                                    @Param("farmerId") Long farmerId,
                                                    @Param("startDate") String startDate,
                                                    @Param("endDate") String endDate);
}