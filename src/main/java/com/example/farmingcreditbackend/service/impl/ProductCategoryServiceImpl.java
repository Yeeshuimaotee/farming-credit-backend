package com.example.farmingcreditbackend.service.impl;

import com.example.farmingcreditbackend.entity.ProductCategory;
import com.example.farmingcreditbackend.mapper.ProductCategoryMapper;
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
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        
        productCategoryMapper.insert(category);
        return category;
    }
    
    @Override
    @Transactional
    public void updateCategory(ProductCategory category) {
        productCategoryMapper.updateById(category);
    }
    
    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        List<ProductCategory> children = productCategoryMapper.selectByParentId(categoryId);
        if (!children.isEmpty()) {
            throw new RuntimeException("请先删除子分类");
        }
        
        productCategoryMapper.deleteById(categoryId);
    }
}
