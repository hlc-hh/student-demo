package com.czjt.controller;

import com.czjt.pojo.Category;
import com.czjt.pojo.Result;
import com.czjt.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取所有分类（状态为启用的）
     */
    @GetMapping
    public Result getAllCategories() {
        List<Category> categories = categoryService.findAll();
        return Result.success(categories);
    }

    /**
     * 根据ID获取分类
     */
    @GetMapping("/{id}")
    public Result getCategoryById(@PathVariable Long id) {
        Category category = categoryService.findById(id);
        if (category != null) {
            return Result.success(category);
        }
        return Result.error("分类不存在");
    }

    /**
     * 根据父级ID获取子分类
     */
    @GetMapping("/parent/{parentId}")
    public Result getCategoriesByParentId(@PathVariable Long parentId) {
        List<Category> categories = categoryService.findByParentId(parentId);
        return Result.success(categories);
    }

    /**
     * 根据层级获取分类
     */
    @GetMapping("/level/{level}")
    public Result getCategoriesByLevel(@PathVariable Integer level) {
        List<Category> categories = categoryService.findByLevel(level);
        return Result.success(categories);
    }
}
