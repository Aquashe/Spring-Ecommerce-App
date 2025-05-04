package com.ecommerce.payload;

import com.ecommerce.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private List<ProductDTO> products;
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalElements;
    private Long totalPages;
    private boolean lastPage ;
}
