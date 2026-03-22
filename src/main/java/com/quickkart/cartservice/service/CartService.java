package com.quickkart.cartservice.service;

import com.quickkart.cartservice.model.CartItem;
import com.quickkart.cartservice.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    public List<CartItem> getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId);
    }

    public CartItem addToCart(CartItem item) {
        // If item already exists for user/product, increment quantity
        return cartRepository.findByUserIdAndProductId(item.getUserId(), item.getProductId())
                .map(existing -> {
                    existing.setQuantity(existing.getQuantity() + item.getQuantity());
                    return cartRepository.save(existing);
                })
                .orElseGet(() -> cartRepository.save(item));
    }

    public Optional<CartItem> updateQuantity(Long itemId, int quantity) {
        return cartRepository.findById(itemId).map(item -> {
            item.setQuantity(quantity);
            return cartRepository.save(item);
        });
    }

    public boolean removeItem(Long itemId) {
        if (cartRepository.existsById(itemId)) {
            cartRepository.deleteById(itemId);
            return true;
        }
        return false;
    }

    @Transactional
    public void clearCart(Long userId) {
        cartRepository.deleteByUserId(userId);
    }
}
