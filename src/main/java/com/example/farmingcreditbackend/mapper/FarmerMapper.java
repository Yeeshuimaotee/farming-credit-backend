package com.example.farmingcreditbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.farmingcreditbackend.entity.Farmer;
import com.example.farmingcreditbackend.dto.FarmerSelectDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface FarmerMapper extends BaseMapper<Farmer> {

    /**
     * 查询指定店铺的农户下拉列表
     */
    @Select("SELECT f.id, f.farmer_name FROM farmer f " +
            "INNER JOIN farmer_store_rel fsr ON f.id = fsr.farmer_id " +
            "WHERE fsr.store_id = #{storeId} AND fsr.status = 1 AND f.status = 1 " +
            "ORDER BY f.farmer_name")
    List<FarmerSelectDTO> selectByStoreId(@Param("storeId") Long storeId);

    /**
     * 增加农户总欠款
     */
    @Update("UPDATE farmer SET total_debt = total_debt + #{amount} WHERE id = #{farmerId}")
    int addDebt(@Param("farmerId") Long farmerId, @Param("amount") BigDecimal amount);
}