package com.example.farmingcreditbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.farmingcreditbackend.entity.Repayment;
import com.example.farmingcreditbackend.dto.OrderRepaymentRecordDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface RepaymentMapper extends BaseMapper<Repayment> {

    /**
     * 根据订单ID查询还款记录，按还款日期倒序
     */
    @Select("SELECT id, repayment_no, repayment_date, repayment_amount, repayment_type, before_debt, after_debt, remark " +
            "FROM repayment " +
            "WHERE order_id = #{orderId} " +
            "ORDER BY repayment_date DESC")
    List<OrderRepaymentRecordDTO> selectByOrderId(@Param("orderId") Long orderId);
}