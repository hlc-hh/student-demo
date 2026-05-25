package com.czjt.service;

import com.czjt.mapper.CategoryMapper;
import com.czjt.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 获取所有分类（状态为启用的）
     */
    @Cacheable(value = "category:all", unless = "#result == null || #result.isEmpty()")
    public List<Category> findAll() {
        return categoryMapper.findAll();
    }

    /**
     * 根据ID获取分类
     */
    @Cacheable(value = "category:id", key = "#id", unless = "#result == null")
    public Category findById(Long id) {
        return categoryMapper.findById(id);
    }

    /**
     * 根据父级ID获取子分类
     */
    @Cacheable(value = "category:parent", key = "#parentId", unless = "#result == null || #result.isEmpty()")
    public List<Category> findByParentId(Long parentId) {
        return categoryMapper.findByParentId(parentId);
    }

    /**
     * 根据层级获取分类
     */
    @Cacheable(value = "category:level", key = "#level", unless = "#result == null || #result.isEmpty()")
    public List<Category> findByLevel(Integer level) {
        return categoryMapper.findByLevel(level);
    }
}