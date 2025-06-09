package com.ecommerce.service;

import com.ecommerce.payload.CartDTO;

public interface CartService {
    CartDTO addProductToCart(Long productId, Integer quantity);
}
