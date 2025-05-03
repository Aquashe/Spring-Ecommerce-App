package com.ecommerce.service;

import com.ecommerce.payload.ProductDTO;
import com.ecommerce.payload.ProductResponse;
import org.springframework.http.HttpStatusCode;

import java.util.List;

public interface ProductService {

    ProductDTO addProduct(long categoryId, ProductDTO productDTO);

    ProductResponse getAllProducts();

    ProductResponse getProductsByCategory(Long categoryId);

    ProductResponse getProductsByKeyord(String keyword);
}
