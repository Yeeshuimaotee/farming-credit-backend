package com.example.farmingcreditbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.farmingcreditbackend.entity.Attachment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 附件 Mapper 接口
 */
@Mapper
public interface AttachmentMapper extends BaseMapper<Attachment> {
    
    /**
     * 根据业务类型和 ID 查询附件
     */
    @Select("SELECT * FROM attachment WHERE business_type = #{businessType} AND business_id = #{businessId} ORDER BY create_time DESC")
    List<Attachment> selectByBusiness(@Param("businessType") String businessType, @Param("businessId") Long businessId);
    
    /**
     * 查询临时附件
     */
    @Select("SELECT * FROM attachment WHERE is_temp = 1 AND expire_time < NOW() ORDER BY create_time ASC LIMIT #{limit}")
    List<Attachment> selectExpiredTempFiles(@Param("limit") int limit);
}
