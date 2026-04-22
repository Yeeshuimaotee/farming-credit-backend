package com.example.farmingcreditbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.farmingcreditbackend.entity.CreditEvaluation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 信用评估 Mapper 接口
 */
@Mapper
public interface CreditEvaluationMapper extends BaseMapper<CreditEvaluation> {
    
    /**
     * 根据农户 ID 查询评估历史
     */
    @Select("SELECT * FROM credit_evaluation WHERE farmer_id = #{farmerId} ORDER BY evaluation_date DESC")
    List<CreditEvaluation> selectByFarmerId(@Param("farmerId") Long farmerId);
    
    /**
     * 根据店铺 ID 查询评估记录
     */
    @Select("SELECT * FROM credit_evaluation WHERE store_id = #{storeId} ORDER BY evaluation_date DESC LIMIT #{limit}")
    List<CreditEvaluation> selectByStoreId(@Param("storeId") Long storeId, @Param("limit") int limit);
    
    /**
     * 查询农户最新评估记录
     */
    @Select("SELECT * FROM credit_evaluation WHERE farmer_id = #{farmerId} ORDER BY evaluation_date DESC LIMIT 1")
    CreditEvaluation selectLatestByFarmerId(@Param("farmerId") Long farmerId);
}
