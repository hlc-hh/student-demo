package com.czjt.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * 收货地址实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    /** 地址ID */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 联系人姓名 */
    private String contactName;

    /** 联系电话 */
    private String contactPhone;

    /** 省 */
    private String province;

    /** 市 */
    private String city;

    /** 区/县 */
    private String district;

    /** 详细地址 */
    private String detailAddress;

    /** 是否默认：0-否, 1-是 */
    private Integer isDefault;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 完整地址（非数据库字段） */
    private transient String fullAddress;
}
