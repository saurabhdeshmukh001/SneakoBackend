package org.genc.app.SneakoAplication.service.api.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.genc.app.SneakoAplication.domain.entity.Cart;
import org.genc.app.SneakoAplication.domain.entity.CartItem;
import org.genc.app.SneakoAplication.dto.CartItemDTO;
import org.genc.app.SneakoAplication.repo.CartItemRepository;
import org.genc.app.SneakoAplication.repo.CartRepository;
import org.genc.app.SneakoAplication.service.api.CartItemService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;

    @Override
    @Transactional
    public CartItemDTO createCartItem(CartItemDTO cartItemDTO) {
        Cart cart = cartRepository.findByUserId(cartItemDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        BigDecimal unitPrice = cartItemDTO.getUnitPrice();
        Long quantity = cartItemDTO.getQuantity();
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));

        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .productId(cartItemDTO.getProductId())
                .unitPrice(unitPrice)
                .quantity(quantity)
                .totalPrice(totalPrice)
                .size(cartItemDTO.getSize()) // ✅ Add this
                .build();

        cartItemRepository.save(cartItem);

        // Recalculate cart totals
        Set<CartItem> cartItems = cartItemRepository.findAll().stream()
                .filter(item -> item.getCart().getId().equals(cart.getId()))
                .collect(Collectors.toSet());

        BigDecimal cartTotalPrice = cartItems.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalItems = cartItems.size();

        cart.setTotalPrice(cartTotalPrice);
        cart.setTotalItem(totalItems);
        cartRepository.save(cart);

        return CartItemDTO.builder()
                .cartItemId(cartItem.getCartItemId())
                .productId(cartItem.getProductId())
                .unitPrice(cartItem.getUnitPrice())
                .quantity(cartItem.getQuantity())
                .totalPrice(cartItem.getTotalPrice())
                .size(cartItem.getSize()) // ✅ Add this
                .build();
    }

    @Override
    @Transactional
    public CartItemDTO updateCartItemQuantity(CartItemDTO cartItemDTO) {
        if (cartItemDTO.getProductId() == null || cartItemDTO.getCartItemId() == null) {
            throw new IllegalArgumentException("Product ID and Cart Item ID are required.");
        }
        if (cartItemDTO.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        CartItem cartItem = cartItemRepository.findByCartItemIdAndProductId(
                cartItemDTO.getCartItemId(),
                cartItemDTO.getProductId()
        ).orElseThrow(() -> new RuntimeException(
                "CartItem not found for CartItem ID: " + cartItemDTO.getCartItemId() +
                        " and Product ID: " + cartItemDTO.getProductId()));

        Long newQuantity = cartItemDTO.getQuantity();
        cartItem.setQuantity(newQuantity);

        BigDecimal unitPrice = cartItem.getUnitPrice(); // use existing stored unit price
        BigDecimal newTotalPrice = unitPrice.multiply(BigDecimal.valueOf(newQuantity));
        cartItem.setTotalPrice(newTotalPrice);

        CartItem updatedItem = cartItemRepository.save(cartItem);

        cartItemDTO.setTotalPrice(updatedItem.getTotalPrice());
        return cartItemDTO;
    }

    @Override
    public CartItemDTO findCartItemById(Long id) {
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CartItem not found"));

        return CartItemDTO.builder()
                .cartItemId(cartItem.getCartItemId())
                .productId(cartItem.getProductId())
                .quantity(cartItem.getQuantity())
                .unitPrice(cartItem.getUnitPrice())
                .totalPrice(cartItem.getTotalPrice())
                .size(cartItem.getSize()) // ✅ Add this
                .build();
    }
}
