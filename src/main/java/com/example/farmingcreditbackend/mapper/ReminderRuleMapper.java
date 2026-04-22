package com.example.farmingcreditbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.farmingcreditbackend.entity.ReminderRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 提醒规则 Mapper 接口
 */
@Mapper
public interface ReminderRuleMapper extends BaseMapper<ReminderRule> {
    
    /**
     * 查询店铺的活跃提醒规则
     */
    @Select("SELECT * FROM reminder_rule WHERE store_id = #{storeId} AND is_active = 1 ORDER BY create_time DESC")
    List<ReminderRule> selectActiveRulesByStoreId(@Param("storeId") Long storeId);
    
    /**
     * 查询所有店铺的活跃规则
     */
    @Select("SELECT * FROM reminder_rule WHERE is_active = 1 ORDER BY store_id, create_time DESC")
    List<ReminderRule> selectAllActiveRules();
}
