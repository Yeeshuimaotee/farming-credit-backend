package com.example.farmingcreditbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.farmingcreditbackend.dto.ProductSelectDTO;
import com.example.farmingcreditbackend.dto.StockCheckRequestDTO;
import com.example.farmingcreditbackend.dto.StockCheckResultDTO;
import com.example.farmingcreditbackend.entity.Product;
import com.example.farmingcreditbackend.entity.ProductCategory;
import com.example.farmingcreditbackend.mapper.ProductMapper;
import com.example.farmingcreditbackend.service.AuthService;
import com.example.farmingcreditbackend.service.ProductCategoryService;
import com.example.farmingcreditbackend.service.ProductService;
import com.example.farmingcreditbackend.service.StoreService;
import com.example.farmingcreditbackend.vo.Result;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/store_owner/products")
@RequiredArgsConstructor
public class StoreProductController {

    private final ProductMapper productMapper;
    private final ProductService productService;
    private final StoreService storeService;
    private final AuthService authService;
    private final ProductCategoryService productCategoryService;

    /**
     * 获取商品列表（分页）
     */
    @GetMapping
    public Result<Page<Product>> getProductList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) Long categoryId) {
        
        Long userId = authService.getCurrentUser().getId();
        Long storeId = storeService.getStoreIdByOwnerId(userId);
        
        Page<Product> productPage = new Page<>(page, size);
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("store_id", storeId);
        
        if (productName != null && !productName.isEmpty()) {
            queryWrapper.like("product_name", productName);
        }
        
        if (categoryId != null) {
            queryWrapper.eq("category_id", categoryId);
        }
        
        queryWrapper.orderByDesc("create_time");
        Page<Product> result = productMapper.selectPage(productPage, queryWrapper);
        
        return Result.success(result);
    }

    /**
     * 获取商品详情
     */
    @GetMapping("/{id}")
    public Result<Product> getProduct(@PathVariable Long id) {
        Long userId = authService.getCurrentUser().getId();
        Long storeId = storeService.getStoreIdByOwnerId(userId);
        
        Product product = productMapper.selectById(id);
        if (product == null || !product.getStoreId().equals(storeId)) {
            return Result.error("商品不存在");
        }
        
        return Result.success(product);
    }

    /**
     * 创建商品
     */
    @PostMapping
    public Result<Void> createProduct(@Valid @RequestBody Product product) {
        Long userId = authService.getCurrentUser().getId();
        Long storeId = storeService.getStoreIdByOwnerId(userId);
        
        // 检查分类状态
        if (product.getCategoryId() != null) {
            ProductCategory category = productCategoryService.getById(product.getCategoryId());
            if (category != null && category.getStatus() == 0 && product.getStatus() == 1) {
                return Result.error("分类已被禁用，无法上架商品");
            }
        }
        
        product.setStoreId(storeId);
        productMapper.insert(product);
        
        return Result.success();
    }

    /**
     * 更新商品
     */
    @PutMapping("/{id}")
    public Result<Void> updateProduct(@PathVariable Long id, @Valid @RequestBody Product product) {
        Long userId = authService.getCurrentUser().getId();
        Long storeId = storeService.getStoreIdByOwnerId(userId);
        
        Product existing = productMapper.selectById(id);
        if (existing == null || !existing.getStoreId().equals(storeId)) {
            return Result.error("商品不存在");
        }
        
        // 检查分类状态
        if (product.getCategoryId() != null) {
            ProductCategory category = productCategoryService.getById(product.getCategoryId());
            if (category != null && category.getStatus() == 0 && product.getStatus() == 1) {
                return Result.error("分类已被禁用，无法上架商品");
            }
        }
        
        product.setId(id);
        productMapper.updateById(product);
        
        return Result.success();
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        Long userId = authService.getCurrentUser().getId();
        Long storeId = storeService.getStoreIdByOwnerId(userId);
        
        Product existing = productMapper.selectById(id);
        if (existing == null || !existing.getStoreId().equals(storeId)) {
            return Result.error("商品不存在");
        }
        
        productMapper.deleteById(id);
        
        return Result.success();
    }

    @GetMapping("/select")
    public Result<List<ProductSelectDTO>> getProductSelect() {
        Long userId = authService.getCurrentUser().getId();
        Long storeId = storeService.getStoreIdByOwnerId(userId);
        List<ProductSelectDTO> list = productMapper.selectByStoreId(storeId);
        return Result.success(list);
    }

    @PostMapping("/check-stock")
    public Result<List<StockCheckResultDTO>> checkStock(@Valid @RequestBody StockCheckRequestDTO request) {
        Long userId = authService.getCurrentUser().getId();
        Long storeId = storeService.getStoreIdByOwnerId(userId);
        List<StockCheckResultDTO> results = productService.checkStock(storeId, request);
        return Result.success(results);
    }

    /**
     * 获取库存预警商品列表
     */
    @GetMapping("/warning-stock")
    public Result<List<Product>> getWarningStockList() {
        Long userId = authService.getCurrentUser().getId();
        Long storeId = storeService.getStoreIdByOwnerId(userId);
        
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("store_id", storeId)
                .eq("status", 1)
                .and(wrapper -> wrapper
                    .apply("stock < min_stock")
                    .or()
                    .apply("stock < safety_stock")
                );
        
        List<Product> warningProducts = productMapper.selectList(queryWrapper);
        return Result.success(warningProducts);
    }
}