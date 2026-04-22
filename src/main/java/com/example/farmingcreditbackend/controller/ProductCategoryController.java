package com.example.farmingcreditbackend.controller;

import com.example.farmingcreditbackend.entity.ProductCategory;
import com.example.farmingcreditbackend.service.ProductCategoryService;
import com.example.farmingcreditbackend.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 商品分类管理控制器
 */
@RestController
@RequestMapping("/store_owner/categories")
@RequiredArgsConstructor
public class ProductCategoryController {

    private final ProductCategoryService categoryService;

    /**
     * 获取分类列表（树形结构）
     */
    @GetMapping
    public Result<List<ProductCategory>> getCategoryList() {
        List<ProductCategory> allCategories = categoryService.getActiveCategories();
        return Result.success(allCategories);
    }

    /**
     * 获取子分类列表
     */
    @GetMapping("/children/{parentId}")
    public Result<List<ProductCategory>> getChildrenCategories(@PathVariable Long parentId) {
        List<ProductCategory> categories = categoryService.getChildrenCategories(parentId);
        return Result.success(categories);
    }

    /**
     * 创建分类
     */
    @PostMapping
    public Result<Void> createCategory(@Valid @RequestBody ProductCategory category) {
        categoryService.createCategory(category);
        return Result.success();
    }

    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    public Result<Void> updateCategory(@PathVariable Long id, @RequestBody ProductCategory category) {
        category.setId(id);
        categoryService.updateCategory(category);
        return Result.success();
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }
}
