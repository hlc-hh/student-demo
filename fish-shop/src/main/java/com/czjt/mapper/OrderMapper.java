package com.czjt.mapper;

import com.czjt.pojo.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {

    List<Order> findByUserId(@Param("userId") Long userId);

    Order findById(@Param("id") Long id);

    Order findByOrderNo(@Param("orderNo") String orderNo);

    int insert(Order order);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    int updateRefundStatus(@Param("id") Long id,
                          @Param("refundStatus") Integer refundStatus,
                          @Param("refundReason") String refundReason);

    int countByUserId(Long userId);
}
