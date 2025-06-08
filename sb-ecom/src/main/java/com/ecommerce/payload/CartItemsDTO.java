package com.ecommerce.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemsDTO {
    private Long cartItemId;
    private CartDTO cartDTO;
    private ProductDTO productDTO;
    private  Integer quantity;
    private Double productPrice;
    private Double discountPrice;
}
