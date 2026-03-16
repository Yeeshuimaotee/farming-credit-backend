package com.example.farmingcreditbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.farmingcreditbackend.entity.FarmerStoreRel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

@Mapper
public interface FarmerStoreRelMapper extends BaseMapper<FarmerStoreRel> {

    @Update("UPDATE farmer_store_rel SET " +
            "current_debt = current_debt + #{amount}, " +
            "total_transactions = total_transactions + 1, " +
            "total_transaction_amount = total_transaction_amount + #{amount}, " +
            "last_transaction_date = CURDATE() " +
            "WHERE farmer_id = #{farmerId} AND store_id = #{storeId}")
    int updateDebtAndStats(@Param("farmerId") Long farmerId, @Param("storeId") Long storeId, @Param("amount") BigDecimal amount);

    @Update("UPDATE farmer_store_rel SET first_transaction_date = CURDATE() " +
            "WHERE farmer_id = #{farmerId} AND store_id = #{storeId} AND first_transaction_date IS NULL")
    int setFirstTransactionDate(@Param("farmerId") Long farmerId, @Param("storeId") Long storeId);

    @Select("SELECT credit_limit, current_debt FROM farmer_store_rel WHERE farmer_id = #{farmerId} AND store_id = #{storeId}")
    FarmerStoreRel selectByFarmerAndStore(@Param("farmerId") Long farmerId, @Param("storeId") Long storeId);

}