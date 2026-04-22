package com.example.farmingcreditbackend.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * 订单商品项DTO
 */
@Data
public class OrderItemDTO {

    @NotNull(message = "商品ID不能为空")
    private Long productId;

    @NotNull(message = "商品数量不能为空")
    @Positive(message = "商品数量必须大于0")
    private Integer quantity;

    @NotNull(message = "商品单价不能为空")
    @Positive(message = "商品单价必须大于0")
    private BigDecimal price;
}
