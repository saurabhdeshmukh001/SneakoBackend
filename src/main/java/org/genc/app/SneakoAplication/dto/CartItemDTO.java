package org.genc.app.SneakoAplication.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDTO {

    @Nullable
    private Long cartItemId;

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Unit price is required")
    @Min(value = 1, message = "Unit price must be at least 1")
    private BigDecimal unitPrice;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Long quantity;

    @Nullable
    private BigDecimal totalPrice;

    @NotNull(message = "User ID is required")
    private Long userId;

    // ✅ New field
    @NotNull(message = "Size is required")
    private Long size;
}