package com.example.farmingcreditbackend.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Data
public class CreateOrderRequestDTO {
    @NotNull(message = "农户ID不能为空")
    private Long farmerId;

    @NotNull(message = "下单日期不能为空")
    private LocalDate orderDate;

    @NotNull(message = "应还日期不能为空")
    private LocalDate dueDate;

    private String remark;

    @NotNull(message = "商品列表不能为空")
    @Size(min = 1, message = "至少需要一个商品")
    private List<OrderItemDto> items;

    @Data
    public static class OrderItemDto {
        @NotNull(message = "商品ID不能为空")
        private Long productId;

        @NotNull(message = "数量不能为空")
        private Integer quantity;

        @NotNull(message = "单价不能为空")
        private java.math.BigDecimal price;
    }
}