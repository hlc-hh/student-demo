package com.czjt.service;

import com.czjt.mapper.CategoryMapper;
import com.czjt.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 获取所有分类（状态为启用的）
     */
    public List<Category> findAll() {
        return categoryMapper.findAll();
    }

    /**
     * 根据ID获取分类
     */
    public Category findById(Long id) {
        return categoryMapper.findById(id);
    }

    /**
     * 根据父级ID获取子分类
     */
    public List<Category> findByParentId(Long parentId) {
        return categoryMapper.findByParentId(parentId);
    }

    /**
     * 根据层级获取分类
     */
    public List<Category> findByLevel(Integer level) {
        return categoryMapper.findByLevel(level);
    }
}