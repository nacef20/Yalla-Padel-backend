package com.shop.productservice.service.impl;

import com.shop.productservice.entity.Cart;
import com.shop.productservice.entity.Product;
import com.shop.productservice.repository.CartRepo;
import com.shop.productservice.repository.ProductRepo;
import com.shop.productservice.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private ProductRepo productRepo;

    @Override
    public Cart saveCart(Integer productId, String userId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Cart cartStatus = cartRepo.findByProductIdAndUserId(productId, userId);

        Cart cart;

        if (ObjectUtils.isEmpty(cartStatus)) {
            cart = new Cart();
            cart.setProduct(product);
            cart.setUserId(userId);
            cart.setQuantity(1);
            cart.setTotalPrice(1 * product.getDiscountPrice());
        } else {
            cart = cartStatus;
            cart.setQuantity(cart.getQuantity() + 1);
            cart.setTotalPrice(cart.getQuantity() * cart.getProduct().getDiscountPrice());
        }

        return cartRepo.save(cart);
    }

    @Override
    public List<Cart> getCartsByUser(String userId) {
        List<Cart> carts = cartRepo.findByUserId(userId);

        Double currentTotalOrderPrice = 0.0;
        List<Cart> updateCarts = new ArrayList<>();
        for (Cart c : carts) {
            Double totalPrice = (c.getProduct().getDiscountPrice() * c.getQuantity());
            c.setTotalPrice(totalPrice);
            currentTotalOrderPrice = currentTotalOrderPrice + totalPrice;
            c.setTotalOrderPrice(currentTotalOrderPrice);
            updateCarts.add(c);
        }

        return updateCarts;
    }

    @Override
    public Integer getCountCart(String userId) {
        return cartRepo.countByUserId(userId);
    }

    @Override
    public void updateQuantity(String sy, Integer cid) {
        Cart cart = cartRepo.findById(cid)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        int updateQuantity;

        if (sy.equalsIgnoreCase("de")) {
            updateQuantity = cart.getQuantity() - 1;

            if (updateQuantity <= 0) {
                cartRepo.delete(cart);
            } else {
                cart.setQuantity(updateQuantity);
                cartRepo.save(cart);
            }
        } else {
            updateQuantity = cart.getQuantity() + 1;
            cart.setQuantity(updateQuantity);
            cartRepo.save(cart);
        }
    }
}
