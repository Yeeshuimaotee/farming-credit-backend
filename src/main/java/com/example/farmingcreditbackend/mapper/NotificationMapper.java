package com.example.farmingcreditbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.farmingcreditbackend.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 消息通知 Mapper 接口
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
    
    /**
     * 根据农户 ID 查询消息
     */
    @Select("SELECT * FROM notification WHERE farmer_id = #{farmerId} ORDER BY create_time DESC LIMIT #{limit}")
    List<Notification> selectByFarmerId(@Param("farmerId") Long farmerId, @Param("limit") int limit);
    
    /**
     * 查询农户未读消息数
     */
    @Select("SELECT COUNT(*) FROM notification WHERE farmer_id = #{farmerId} AND is_read = 0")
    int countUnread(@Param("farmerId") Long farmerId);
    
    /**
     * 查询农户未读消息
     */
    @Select("SELECT * FROM notification WHERE farmer_id = #{farmerId} AND is_read = 0 ORDER BY create_time DESC LIMIT #{limit}")
    List<Notification> selectUnread(@Param("farmerId") Long farmerId, @Param("limit") int limit);
}
