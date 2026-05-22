package com.czjt.service;

import com.czjt.mapper.OrderMapper;
import com.czjt.pojo.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    public List<Order> getUserOrders(Long userId) {
        return orderMapper.findByUserId(userId);
    }

    public Order getOrderById(Long id) {
        return orderMapper.findById(id);
    }

    public Order getOrderByOrderNo(String orderNo) {
        return orderMapper.findByOrderNo(orderNo);
    }


    @Transactional
    public void updateOrderStatus(Long id, Integer status) {
        orderMapper.updateStatus(id, status);
    }


    /**
     * 创建订单
     */
    @Transactional
    public Order createOrder(Long userId, BigDecimal totalPrice, BigDecimal freightPrice,
                             BigDecimal payPrice, String receiverName, String receiverPhone,
                             String receiverAddress, List<Map<String, Object>> items) {

        // 生成订单号
        String orderNo = generateOrderNo();

        // 创建订单
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalPrice(totalPrice);
        order.setPayPrice(payPrice);
        order.setFreightPrice(freightPrice);
        order.setStatus(0); // 待支付
        order.setRefundStatus(0);
        order.setReceiverName(receiverName);
        order.setReceiverPhone(receiverPhone);
        order.setReceiverAddress(receiverAddress);

        orderMapper.insert(order);

        // TODO: 保存订单商品明细到 order_item 表（如果有订单详情表）
        // for (Map<String, Object> item : items) {
        //     OrderItem orderItem = new OrderItem();
        //     orderItem.setOrderId(order.getId());
        //     orderItem.setProductId(Long.valueOf(item.get("productId").toString()));
        //     orderItem.setProductName((String) item.get("productName"));
        //     orderItem.setProductPrice(new BigDecimal(item.get("productPrice").toString()));
        //     orderItem.setQuantity(Integer.valueOf(item.get("quantity").toString()));
        //     orderItem.setTotalPrice(new BigDecimal(item.get("subtotal").toString()));
         //  orderItemMapper.insert(orderItem);
      // }

        return order;
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = sdf.format(new Date());
        String random = String.format("%04d", (int)(Math.random() * 10000));
        return "ORD" + timestamp + random;
    }


    @Transactional
    public void applyRefund(Long orderId, String reason) {
        Order order = orderMapper.findById(orderId);


        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (order.getStatus() == 40) {
            throw new RuntimeException("订单已取消，无法申请退款");
        }

        if (order.getRefundStatus() != null && order.getRefundStatus() != 0) {
            throw new RuntimeException("该订单已有退款申请在处理中");
        }

        orderMapper.updateRefundStatus(orderId, 1, reason);
    }

    public String getStatusDescription(Integer status) {
        if (status == null) {
            return "未知状态";
        }

        switch (status) {
            case 0:
                return "待支付";
            case 10:
                return "待发货";
            case 20:
                return "待收货";
            case 30:
                return "已完成";
            case 40:
                return "已取消";
            default:
                return "未知状态";
        }
    }

    public String getRefundStatusDescription(Integer refundStatus) {
        if (refundStatus == null || refundStatus == 0) {
            return "无退款";
        }

        switch (refundStatus) {
            case 1:
                return "申请退款";
            case 2:
                return "退款中";
            case 3:
                return "退款成功";
            case 4:
                return "退款拒绝";
            default:
                return "未知状态";
        }
    }
}
