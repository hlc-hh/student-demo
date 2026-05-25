package com.czjt.service;

import com.czjt.mapper.CartMapper;
import com.czjt.pojo.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartMapper cartMapper;

    /**
     * 添加商品到购物车
     */
    @Transactional
    @CacheEvict(value = {"cart:list", "cart:checked"}, key = "#userId")
    public void addToCart(Long userId, Long productId, Integer quantity) {
        Cart existingCart = cartMapper.findByUserIdAndProductId(userId, productId);

        if (existingCart != null) {
            cartMapper.increaseQuantity(userId, productId, quantity);
        } else {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(quantity);
            cart.setChecked(1);
            cartMapper.insert(cart);
        }
    }

    /**
     * 获取用户购物车列表
     */
    @Cacheable(value = "cart:list", key = "#userId", unless = "#result == null")
    public List<Cart> getCartList(Long userId) {
        return cartMapper.findByUserId(userId);
    }

    /**
     * 更新购物车商品数量
     */
    @Transactional
    @CacheEvict(value = {"cart:list", "cart:checked"}, allEntries = true)
    public void updateQuantity(Long cartId, Integer quantity) {
        if (quantity <= 0) {
            cartMapper.deleteById(cartId);
        } else {
            Cart cart = new Cart();
            cart.setId(cartId);
            cart.setQuantity(quantity);
            cartMapper.updateQuantity(cart);
        }
    }

    /**
     * 更新购物车商品选中状态
     */
    @CacheEvict(value = {"cart:list", "cart:checked"}, allEntries = true)
    public void updateChecked(Long cartId, Integer checked) {
        Cart cart = new Cart();
        cart.setId(cartId);
        cart.setChecked(checked);
        cartMapper.updateChecked(cart);
    }

    /**
     * 从购物车删除商品（根据购物车项ID）
     */
    @CacheEvict(value = {"cart:list", "cart:checked"}, allEntries = true)
    public void removeById(Long cartId) {
        cartMapper.deleteById(cartId);
    }

    /**
     * 从购物车删除商品（根据用户ID和商品ID）
     */
    @CacheEvict(value = {"cart:list", "cart:checked"}, key = "#userId")
    public void removeByProduct(Long userId, Long productId) {
        cartMapper.deleteByUserIdAndProductId(userId, productId);
    }

    /**
     * 批量删除购物车商品
     */
    @Transactional
    @CacheEvict(value = {"cart:list", "cart:checked"}, allEntries = true)
    public void batchRemove(List<Long> cartIds) {
        if (cartIds != null && !cartIds.isEmpty()) {
            cartMapper.batchDelete(cartIds);
        }
    }

    /**
     * 清空用户购物车
     */
    @CacheEvict(value = {"cart:list", "cart:checked"}, key = "#userId")
    public void clearCart(Long userId) {
        cartMapper.deleteByUserId(userId);
    }

    /**
     * 获取用户选中的购物车商品
     */
    @Cacheable(value = "cart:checked", key = "#userId", unless = "#result == null")
    public List<Cart> getCheckedItems(Long userId) {
        return cartMapper.findCheckedByUserId(userId);
    }

    /**
     * 批量更新选中状态
     */
    @Transactional
    @CacheEvict(value = {"cart:list", "cart:checked"}, allEntries = true)
    public void batchUpdateChecked(List<Long> cartIds, Integer checked) {
        if (cartIds != null && !cartIds.isEmpty()) {
            cartMapper.batchUpdateChecked(cartIds, checked);
        }
    }

    /**
     * 全选/取消全选
     */
    @Transactional
    @CacheEvict(value = {"cart:list", "cart:checked"}, key = "#userId")
    public void checkAll(Long userId, Integer checked) {
        List<Cart> cartList = cartMapper.findByUserId(userId);
        if (cartList != null && !cartList.isEmpty()) {
            List<Long> cartIds = cartList.stream()
                    .map(Cart::getId)
                    .collect(Collectors.toList());
            cartMapper.batchUpdateChecked(cartIds, checked);
        }
    }
}
