package com.ecommerce.repository;

import com.ecommerce.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ecommerce.model.Cart;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {
    @Query("SELECT c FROM Cart c where c.user.email = ?1")
    Cart findCartByEmail(String email);

    @Query("SELECT c FROM Cart c where c.user.email = ?1 and c.cartId = ?2")
    Cart findCartByEmailAndCartId(String emailId, Long cartId);

    @Query("SELECT c FROM Cart c JOIN FETCH c.cartItems ci JOIN FETCH ci.product p where p.productId = ?1")
    List<Cart> findCartsByProductId(Long productId);
}
