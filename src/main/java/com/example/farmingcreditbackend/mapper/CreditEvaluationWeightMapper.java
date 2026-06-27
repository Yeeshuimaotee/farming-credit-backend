package com.example.farmingcreditbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.farmingcreditbackend.entity.CreditEvaluationWeight;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 信用评估权重配置 Mapper 接口
 */
@Mapper
public interface CreditEvaluationWeightMapper extends BaseMapper<CreditEvaluationWeight> {
    
    /**
     * 根据店铺 ID 查询权重配置
     */
    @Select("SELECT * FROM credit_evaluation_weight WHERE store_id = #{storeId}")
    CreditEvaluationWeight selectByStoreId(@Param("storeId") Long storeId);
}
