package com.example.farmingcreditbackend.service;

import com.example.farmingcreditbackend.entity.ProductCategory;

import java.util.List;

/**
 * 商品分类服务接口
 */
public interface ProductCategoryService {
    
    /**
     * 获取所有启用的分类（树形结构）
     */
    List<ProductCategory> getActiveCategories();
    
    /**
     * 根据父 ID 获取子分类
     */
    List<ProductCategory> getChildrenCategories(Long parentId);
    
    /**
     * 创建分类
     */
    ProductCategory createCategory(ProductCategory category);
    
    /**
     * 更新分类
     */
    void updateCategory(ProductCategory category);
    
    /**
     * 删除分类
     */
    void deleteCategory(Long categoryId);
}
