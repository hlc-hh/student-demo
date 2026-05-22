package com.czjt.controller;

import com.czjt.pojo.Order;
import com.czjt.pojo.Result;
import com.czjt.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @GetMapping
    public Result<List<Order>> getUserOrders(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        logger.info("查询用户订单列表, userId: {}", userId);
        List<Order> orders = orderService.getUserOrders(userId);

        enrichOrderStatus(orders);

        return Result.success(orders);
    }

    /**
     * 创建订单
     */
    @PostMapping("/create")
    public Result<Map<String, Object>> createOrder(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        try {
            // 获取订单基本信息
            BigDecimal totalPrice = new BigDecimal(params.get("totalPrice").toString());
            BigDecimal freightPrice = new BigDecimal(params.get("freightPrice").toString());
            BigDecimal payPrice = new BigDecimal(params.get("payPrice").toString());
            String receiverName = (String) params.get("receiverName");
            String receiverPhone = (String) params.get("receiverPhone");
            String receiverAddress = (String) params.get("receiverAddress");

            // 获取商品列表
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) params.get("items");

            if (items == null || items.isEmpty()) {
                return Result.error("订单商品不能为空");
            }

            // 创建订单
            Order order = orderService.createOrder(userId, totalPrice, freightPrice, payPrice,
                    receiverName, receiverPhone, receiverAddress, items);

            Map<String, Object> result = new HashMap<>();
            result.put("orderId", order.getId());
            result.put("orderNo", order.getOrderNo());
            result.put("payPrice", order.getPayPrice());

            logger.info("订单创建成功, userId: {}, orderNo: {}", userId, order.getOrderNo());
            return Result.success("订单创建成功", result);

        } catch (Exception e) {
            logger.error("创建订单失败, userId: {}", userId, e);
            return Result.error("创建订单失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<Order> getOrderDetail(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        logger.info("查询订单详情, orderId: {}, userId: {}", id, userId);

        Order order = validateOrderOwnership(id, userId);


        enrichOrderSingleStatus(order);

        return Result.success(order);
    }

    @PostMapping("/{id}/refund")
    public Result<Void> applyRefund(@PathVariable Long id,
                                   @RequestBody Map<String, String> params,
                                   HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        logger.info("申请退款, orderId: {}, userId: {}", id, userId);

        Order order = validateOrderOwnership(id, userId);


        String reason = params.get("reason");
        if (reason == null || reason.trim().isEmpty()) {
            return Result.error("退款原因不能为空");
        }

        try {
            orderService.applyRefund(id, reason.trim());
            logger.info("退款申请成功, orderId: {}", id);
            return Result.success("退款申请提交成功", null);
        } catch (RuntimeException e) {
            logger.warn("退款申请失败, orderId: {}, reason: {}", id, e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}/status")
    public Result<Map<String, Object>> getOrderStatus(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        logger.info("查询订单状态, orderId: {}, userId: {}", id, userId);

        Order order = validateOrderOwnership(id, userId);


        Map<String, Object> statusInfo = buildStatusInfo(order);

        return Result.success(statusInfo);
    }


    // 取消订单
    @PostMapping("/{id}/cancel")
    public Result<Void> cancelOrder(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        Order order = validateOrderOwnership(id, userId);

        if (order.getStatus() != 0) {
            return Result.error("只有待支付的订单可以取消");
        }

        orderService.updateOrderStatus(id, 40);
        logger.info("订单取消成功, orderId: {}", id);
        return Result.success("订单已取消", null);
    }

    // 确认收货
    @PostMapping("/{id}/receive")
    public Result<Void> confirmReceive(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }

        Order order = validateOrderOwnership(id, userId);

        if (order.getStatus() != 20) {
            return Result.error("只有待收货的订单可以确认收货");
        }

        orderService.updateOrderStatus(id, 30);
        logger.info("确认收货成功, orderId: {}", id);
        return Result.success("确认收货成功", null);
    }


    private Long getCurrentUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    private Order validateOrderOwnership(Long orderId, Long userId) {
        Order order = orderService.getOrderById(orderId);

        if (order == null) {
            logger.warn("订单不存在, orderId: {}", orderId);
            throw new IllegalArgumentException("订单不存在");
        }

        if (!order.getUserId().equals(userId)) {
            logger.warn("无权访问订单, orderId: {}, userId: {}, orderUserId: {}",
                       orderId, userId, order.getUserId());
            throw new SecurityException("无权查看此订单");
        }

        return order;
    }

    private void enrichOrderStatus(List<Order> orders) {
        if (orders != null && !orders.isEmpty()) {
            orders.forEach(this::enrichOrderSingleStatus);
        }
    }

    private void enrichOrderSingleStatus(Order order) {
        if (order != null) {
            order.setStatusDescription(orderService.getStatusDescription(order.getStatus()));
            order.setRefundStatusDescription(orderService.getRefundStatusDescription(order.getRefundStatus()));
        }
    }

    private Map<String, Object> buildStatusInfo(Order order) {
        Map<String, Object> statusInfo = new HashMap<>();
        statusInfo.put("orderId", order.getId());
        statusInfo.put("orderNo", order.getOrderNo());
        statusInfo.put("status", order.getStatus());
        statusInfo.put("statusDescription", orderService.getStatusDescription(order.getStatus()));
        statusInfo.put("refundStatus", order.getRefundStatus());
        statusInfo.put("refundStatusDescription", orderService.getRefundStatusDescription(order.getRefundStatus()));
        statusInfo.put("createTime", order.getCreateTime());
        statusInfo.put("paymentTime", order.getPaymentTime());
        statusInfo.put("deliveryTime", order.getDeliveryTime());
        statusInfo.put("receiveTime", order.getReceiveTime());
        statusInfo.put("refundApplyTime", order.getRefundApplyTime());
        statusInfo.put("refundCompleteTime", order.getRefundCompleteTime());
        statusInfo.put("refundReason", order.getRefundReason());
        return statusInfo;
    }
}
