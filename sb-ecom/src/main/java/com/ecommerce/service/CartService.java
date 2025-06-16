package com.ecommerce.service;

import com.ecommerce.payload.CartDTO;
import jakarta.transaction.Transactional;

import java.util.List;

public interface CartService {
    CartDTO addProductToCart(Long productId, Integer quantity);


    List<CartDTO> getAllCarts();

    CartDTO getCart(String emailId, Long cartId);

    @Transactional
    CartDTO updateProductQuantityInCart(Long productId, Integer quantity);

    @Transactional
    String deleteProductFromCart(Long cartId, Long productId);

    @Transactional
    void updateProductInCart(Long cartId, Long productId);
}
