package com.ecommerce.service;

import com.ecommerce.exceptions.APIException;
import com.ecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.model.Cart;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.Product;
import com.ecommerce.payload.CartDTO;
import com.ecommerce.payload.ProductDTO;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class CartServiceImplementation implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        /*
        1. Find existing cart or create one
        2. Retrieve product details
        3. Perform validations
        4. Create Cart Item
        5. Save the Cart Item
        6. Return the updated cart*/
        Cart cart = createCart();


        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product","productId",productId));


        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartID(productId,cart.getCartId());


        if(cartItem != null)
            throw new APIException("Product",product.getProductName());
        if(product.getQuantity() == 0)
            throw new APIException("Product","Name",product.getProductName());
        if(product.getQuantity() < quantity)
            throw new APIException("Product",product.getProductName(),product.getQuantity());


        CartItem newCartItem = new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setQuantity(quantity);
        newCartItem.setCart(cart);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());
        cartItemRepository.save(newCartItem);


        product.setQuantity(product.getQuantity());
        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));
        productRepository.save(product);
        cartRepository.save(cart);


        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<CartItem> cartItems = cart.getCartItems();
        Stream<ProductDTO> productDTOStream = cartItems.stream()
                .map(item ->{
                    ProductDTO productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
                    productDTO.setQuantity(item.getQuantity());
                    return productDTO;
                });
        cartDTO.setProductDTOS(productDTOStream.toList());

        return cartDTO;
    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        if(carts.isEmpty())
            throw new APIException("Carts");
        List<CartDTO> cartDTOS = carts.stream()
                .map((cart -> {
                    CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
                    cart.getCartItems().forEach(cartItem -> {
                        cartItem.getProduct().setQuantity(cartItem.getQuantity());
                    });
                    List<ProductDTO> productDTOS = cart.getCartItems().stream()
                            .map(cartItem -> {
                               ProductDTO productDTO = modelMapper.map(cartItem.getProduct(), ProductDTO.class);
                               return  productDTO;
                            }).toList();
                    cartDTO.setProductDTOS(productDTOS);
                    return cartDTO;
                })).toList();
        return cartDTOS;
    }

    @Override
    public CartDTO getCart(String emailId, Long cartId) {
        Cart cart = cartRepository.findCartByEmailAndCartId(emailId, cartId);
        if(cart == null)
            throw new ResourceNotFoundException("Cart","cartId",cartId);
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        cart.getCartItems().forEach(cartItem -> {
            cartItem.getProduct().setQuantity(cartItem.getQuantity());
        });
        List<ProductDTO> productDTOS = cart.getCartItems().stream().map(
                cartItem -> {
                    ProductDTO productDTO = modelMapper.map(cartItem.getProduct(), ProductDTO.class);
                    return  productDTO;
                }).toList();
        cartDTO.setProductDTOS(productDTOS);
        return cartDTO;
    }

    @Transactional
    @Override
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {
        Cart cart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        Long cartId = cart.getCartId();
        Cart existingCart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart","cartId",cartId));


        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product","productId",productId));
        if(product.getQuantity() == 0)
            throw new APIException("Product","Name",product.getProductName());
        if(product.getQuantity() < quantity)
            throw new APIException("Product",product.getProductName(),product.getQuantity());

        //For updating the quantity we need the cartItem
        CartItem newCartItem = cartItemRepository.findCartItemByProductIdAndCartID(productId,cartId);
        if(newCartItem == null)
            throw new APIException("Cart Item","ProductName",product.getProductName());


        newCartItem.setProductPrice(product.getSpecialPrice());
        newCartItem.setQuantity(newCartItem.getQuantity() + quantity);
        newCartItem.setDiscount(product.getDiscount());
        cart.setTotalPrice(newCartItem.getProductPrice() + (product.getPrice() * quantity));
        cartRepository.save(cart);
        CartItem updatedCartItem = cartItemRepository.save(newCartItem);

        //If product is empty , there is no need for the cart item
        if(updatedCartItem.getQuantity() ==0)
            cartItemRepository.deleteById(updatedCartItem.getCartItemId());

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<ProductDTO> productDTOS = cart.getCartItems().stream()
                .map(cartItem -> {
                    ProductDTO productDTO = modelMapper.map(cartItem.getProduct(), ProductDTO.class);
                    productDTO.setQuantity(cartItem.getQuantity());
                    return productDTO;
                }).toList();
        cartDTO.setProductDTOS(productDTOS);
        return cartDTO;
    }


    private Cart createCart(){
        Cart UserCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if(UserCart != null)
            return UserCart;
        else{
            Cart newCart = new Cart();
            newCart.setTotalPrice(0.0);
            newCart.setUser(authUtil.loggedInUser());
            return cartRepository.save(newCart);
        }
    }
}
