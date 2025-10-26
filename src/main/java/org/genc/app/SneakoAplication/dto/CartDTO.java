package org.genc.app.SneakoAplication.dto;

import java.math.BigDecimal;
import java.util.Set;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Value; // Using @Value for immutable DTOs is often preferred
import org.genc.app.SneakoAplication.dto.CartItemDTO;

/**
 * Data Transfer Object for the Cart entity.
 * Simplifies the entity relationships by using IDs for User and nested CartItemDTOs.
 */
@Value
public class CartDTO {

    // Primary identifier of the cart
    @Nullable
    private final Long id;

    // Foreign key reference to the Users entity, simplified to an ID
    @NotNull(message = "Enter the user Id")
    private final Long userId;

    // Collection of items in the cart, represented by their DTOs
    private final Set<CartItemDTO> cartItems;

    // The calculated total price of all items
    private final BigDecimal totalPrice;

    // The total number of unique items/products
    private final int totalItem;

}
