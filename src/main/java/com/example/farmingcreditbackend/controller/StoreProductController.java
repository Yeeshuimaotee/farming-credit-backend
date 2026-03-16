package com.example.farmingcreditbackend.controller;

import com.example.farmingcreditbackend.dto.ProductSelectDTO;
import com.example.farmingcreditbackend.dto.StockCheckRequestDTO;
import com.example.farmingcreditbackend.dto.StockCheckResultDTO;
import com.example.farmingcreditbackend.mapper.ProductMapper;
import com.example.farmingcreditbackend.service.AuthService;
import com.example.farmingcreditbackend.service.ProductService;
import com.example.farmingcreditbackend.service.StoreService;
import com.example.farmingcreditbackend.vo.Result;
import jakarta.validation.Valid;
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
}