package com.example.farmingcreditbackend.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class StockCheckRequestDTO {
    @NotNull(message = "商品列表不能为空")
    private List<StockCheckItem> items;

    @Data
    public static class StockCheckItem {
        @NotNull(message = "商品ID不能为空")
        private Long productId;

        @NotNull(message = "数量不能为空")
        private Integer quantity;
    }
}