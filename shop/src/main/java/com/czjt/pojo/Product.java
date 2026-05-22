package com.czjt.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    private Integer id;
    private String name;
    private Long categoryId;
    private String subName;
    private String price;
    private Integer stock;
    private Integer sales;
    private String specs;
    private Integer status;
    private String createTime;
    private String updateTime;


}