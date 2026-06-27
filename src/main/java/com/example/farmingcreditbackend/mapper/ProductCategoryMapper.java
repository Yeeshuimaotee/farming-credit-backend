package com.example.farmingcreditbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.farmingcreditbackend.entity.ProductCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 商品分类 Mapper 接口
 */
@Mapper
public interface ProductCategoryMapper extends BaseMapper<ProductCategory> {
    
    /**
     * 查询所有分类
     */
    @Select("SELECT * FROM product_category ORDER BY sort_order ASC, parent_id ASC, id ASC")
    List<ProductCategory> selectActiveCategories();
    
    /**
     * 根据父 ID 查询子分类
     */
    @Select("SELECT * FROM product_category WHERE parent_id = #{parentId} ORDER BY sort_order ASC")
    List<ProductCategory> selectByParentId(@Param("parentId") Long parentId);
}
