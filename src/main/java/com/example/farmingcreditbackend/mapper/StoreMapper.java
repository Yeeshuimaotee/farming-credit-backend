package com.example.farmingcreditbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.farmingcreditbackend.entity.Store;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StoreMapper extends BaseMapper<Store> {

    /**
     * 根据店主ID获取店铺ID
     */
    @Select("SELECT id FROM store WHERE owner_id = #{ownerId} AND status = 1")
    Long getStoreIdByOwnerId(@Param("ownerId") Long ownerId);
}