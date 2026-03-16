package com.example.farmingcreditbackend.service;

import com.example.farmingcreditbackend.dto.StockCheckRequestDTO;
import com.example.farmingcreditbackend.dto.StockCheckResultDTO;
import com.example.farmingcreditbackend.entity.Product;
import com.example.farmingcreditbackend.exception.BusinessException;
import com.example.farmingcreditbackend.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;

    /**
     * 批量检查商品库存
     * @param storeId 当前店铺ID
     * @param request 包含商品ID和数量的列表
     * @return 每个商品的检查结果
     */
    public List<StockCheckResultDTO> checkStock(Long storeId, StockCheckRequestDTO request) {
        // 提取商品ID列表
        List<Long> productIds = request.getItems().stream()
                .map(StockCheckRequestDTO.StockCheckItem::getProductId)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询商品信息（只查需要的字段）
        List<Product> products = productMapper.selectBatchByIds(storeId, productIds);
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // 构建结果
        return request.getItems().stream()
                .map(item -> {
                    StockCheckResultDTO result = new StockCheckResultDTO();
                    result.setProductId(item.getProductId());
                    result.setRequired(item.getQuantity());

                    Product product = productMap.get(item.getProductId());
                    if (product == null) {
                        throw new BusinessException("商品不存在或不属于当前店铺，ID：" + item.getProductId());
                    }
                    result.setProductName(product.getProductName());
                    result.setStock(product.getStock());
                    result.setSufficient(product.getStock() >= item.getQuantity());
                    return result;
                })
                .collect(Collectors.toList());
    }
}