package com.czjt.mapper;

import com.czjt.pojo.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CartMapper {

    int insert(Cart cart);

    List<Cart> findByUserId(Long userId);

    Cart findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    int updateQuantity(Cart cart);

    int updateChecked(Cart cart);

    int deleteById(Long id);

    int deleteByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    int deleteByUserId(Long userId);

    List<Cart> findCheckedByUserId(Long userId);

    int increaseQuantity(@Param("userId") Long userId, @Param("productId") Long productId, @Param("quantity") Integer quantity);

    int batchDelete(@Param("cartIds") List<Long> cartIds);

    int batchUpdateChecked(@Param("cartIds") List<Long> cartIds, @Param("checked") Integer checked);
}
