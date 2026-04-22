package com.example.farmingcreditbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.farmingcreditbackend.entity.ReminderLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 提醒记录 Mapper 接口
 */
@Mapper
public interface ReminderLogMapper extends BaseMapper<ReminderLog> {
    
    /**
     * 根据农户 ID 查询提醒记录
     */
    @Select("SELECT * FROM reminder_log WHERE farmer_id = #{farmerId} ORDER BY reminder_date DESC LIMIT #{limit}")
    List<ReminderLog> selectByFarmerId(@Param("farmerId") Long farmerId, @Param("limit") int limit);
    
    /**
     * 查询待发送的提醒
     */
    @Select("SELECT * FROM reminder_log WHERE send_status = 'PENDING' ORDER BY create_time ASC LIMIT #{limit}")
    List<ReminderLog> selectPendingReminders(@Param("limit") int limit);
}
