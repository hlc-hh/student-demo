package com.czjt.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    /** 订单ID */
    private Long id;

    /** 订单编号(业务主键) */
    private String orderNo;

    /** 用户ID */
    private Long userId;

    /** 订单总金额 */
    private BigDecimal totalPrice;

    /** 应付金额(扣除优惠后) */
    private BigDecimal payPrice;

    /** 运费 */
    private BigDecimal freightPrice;

    /** 订单状态：0-待支付, 10-待发货, 20-待收货, 30-已完成, 40-已取消 */
    private Integer status;

    /** 支付时间 */
    private LocalDateTime paymentTime;

    /** 发货时间 */
    private LocalDateTime deliveryTime;

    /** 收货时间 */
    private LocalDateTime receiveTime;

    /** 收货人姓名 */
    private String receiverName;

    /** 收货人电话 */
    private String receiverPhone;

    /** 收货地址 */
    private String receiverAddress;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 退款状态：0-无退款, 1-申请退款, 2-退款中, 3-退款成功, 4-退款拒绝 */
    private Integer refundStatus;

    /** 退款申请时间 */
    private LocalDateTime refundApplyTime;

    /** 退款完成时间 */
    private LocalDateTime refundCompleteTime;

    /** 退款原因 */
    private String refundReason;

    /** 订单状态描述（非数据库字段） */
    private transient String statusDescription;

    /** 退款状态描述（非数据库字段） */
    private transient String refundStatusDescription;
}
