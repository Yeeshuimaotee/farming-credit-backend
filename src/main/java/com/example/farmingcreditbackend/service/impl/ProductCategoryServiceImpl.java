package com.example.farmingcreditbackend.service.impl;

import com.example.farmingcreditbackend.entity.ProductCategory;
import com.example.farmingcreditbackend.mapper.ProductCategoryMapper;
import com.example.farmingcreditbackend.mapper.ProductMapper;
import com.example.farmingcreditbackend.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商品分类服务实现类
 */
@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {
    
    @Autowired
    private ProductCategoryMapper productCategoryMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Override
    public List<ProductCategory> getActiveCategories() {
        return productCategoryMapper.selectActiveCategories();
    }
    
    @Override
    public List<ProductCategory> getChildrenCategories(Long parentId) {
        return productCategoryMapper.selectByParentId(parentId);
    }
    
    @Override
    @Transactional
    public ProductCategory createCategory(ProductCategory category) {
        // 当parentId为0或null时，设置为null，避免外键约束失败
        if (category.getParentId() == null || category.getParentId() == 0) {
            category.setParentId(null);
        }
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        
        // 确保parentId字段被包含在INSERT语句中
        productCategoryMapper.insert(category);
        return category;
    }
    
    @Override
    @Transactional
    public void updateCategory(ProductCategory category) {
        // 当parentId为0或null时，设置为null，避免外键约束失败
        if (category.getParentId() == null || category.getParentId() == 0) {
            category.setParentId(null);
        }
        
        // 检查是否是禁用操作
        ProductCategory existing = productCategoryMapper.selectById(category.getId());
        if (existing != null && existing.getStatus() == 1 && category.getStatus() == 0) {
            // 禁用分类时，将该分类下的所有商品状态改为下架
            productMapper.updateStatusByCategoryId(category.getId(), 0);
        }
        
        productCategoryMapper.updateById(category);
    }
    
    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        // 检查是否有子分类
        List<ProductCategory> children = productCategoryMapper.selectByParentId(categoryId);
        if (!children.isEmpty()) {
            throw new RuntimeException("请先删除子分类");
        }
        
        // 检查分类下是否有商品（无论是否下架）
        int productCount = productMapper.countByCategoryId(categoryId);
        if (productCount > 0) {
            throw new RuntimeException("该分类下有" + productCount + "个商品，请先将商品下架后再删除分类");
        }
        
        productCategoryMapper.deleteById(categoryId);
    }
    
    @Override
    public ProductCategory getById(Long categoryId) {
        return productCategoryMapper.selectById(categoryId);
    }
}
