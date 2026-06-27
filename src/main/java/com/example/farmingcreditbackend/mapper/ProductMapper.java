package com.example.farmingcreditbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.farmingcreditbackend.entity.Product;
import com.example.farmingcreditbackend.dto.ProductSelectDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    /**
     * 查询指定店铺的上架商品下拉列表
     */
    @Select("SELECT id, product_name, sale_price FROM product " +
            "WHERE store_id = #{storeId} AND status = 1 " +
            "ORDER BY product_name")
    List<ProductSelectDTO> selectByStoreId(@Param("storeId") Long storeId);

    /**
     * 扣减库存（带库存检查）
     * @return 影响行数，0表示库存不足或商品不存在
     */
    @Update("UPDATE product SET stock = stock - #{quantity} " +
            "WHERE id = #{productId} AND stock >= #{quantity}")
    int reduceStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    /**
     * 批量查询指定店铺的商品信息（用于库存检查）
     */
    @Select("<script>" +
            "SELECT id, product_name, stock FROM product " +
            "WHERE store_id = #{storeId} AND id IN " +
            "<foreach collection='productIds' item='id' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</script>")
    List<Product> selectBatchByIds(@Param("storeId") Long storeId, @Param("productIds") List<Long> productIds);
    
    /**
     * 检查分类下是否有商品
     */
    @Select("SELECT COUNT(*) FROM product WHERE category_id = #{categoryId}")
    int countByCategoryId(@Param("categoryId") Long categoryId);
    
    /**
     * 获取分类下的商品列表
     */
    @Select("SELECT * FROM product WHERE category_id = #{categoryId}")
    List<Product> selectByCategoryId(@Param("categoryId") Long categoryId);
    
    /**
     * 根据分类ID更新商品状态
     */
    @Update("UPDATE product SET status = #{status} WHERE category_id = #{categoryId}")
    void updateStatusByCategoryId(@Param("categoryId") Long categoryId, @Param("status") Integer status);
}