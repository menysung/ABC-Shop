package org.zerock.b02.service;

import org.zerock.b02.dto.CartDTO;

import java.util.List;

public interface CartService {
    void addToCart(Long productId, int quantity, String membername);
    List<CartDTO> getCart(String membername);
    void updateCart(Long productId, int quantity, String membername);
    void deleteCart(Long productId, String membername);
    int getCartItemCount(String membername);


}