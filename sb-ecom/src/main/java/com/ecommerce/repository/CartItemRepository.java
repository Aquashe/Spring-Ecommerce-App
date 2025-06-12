package com.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecommerce.model.CartItem;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    @Query("SELECT ci from CartItem ci where ci.cart.cartId = ?2 and  ci.product.productId = ?1")
    CartItem findCartItemByProductIdAndCartID(Long productId, Long cartId);

    @Modifying
    @Query("Delete FROM CartItem ci where ci.cart.cartId = ?2 and ci.product.productId = ?1")
    void deleteCartItemByProductIdAndCartId(Long productId, Long cartId);
}
