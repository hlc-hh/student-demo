package com.czjt.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 商品分类实体类
 * @description 商品分类表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category{
    private static final long serialVersionUID = 1L;

    /**
     * 分类ID
     */
    private Long id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 父分类ID (0为一级分类)
     */
    private Long parentId;

    /**
     * 层级 (1-一级, 2-二级...)
     */
    private Integer level;

    /**
     * 分类图标
     */
    private String icon;

    /**
     * 排序权重
     */
    private Integer sort;

    /**
     * 状态：0-隐藏, 1-显示
     */
    private Integer status;
}