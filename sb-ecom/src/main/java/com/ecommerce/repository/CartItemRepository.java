package com.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecommerce.model.CartItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    @Query("SELECT ci from CartItem ci where ci.cart.cartId = ?2 and  ci.product.productId = ?1")
    CartItem findCartItemByProductIdAndCartID(Long productId, Long cartId);
}
