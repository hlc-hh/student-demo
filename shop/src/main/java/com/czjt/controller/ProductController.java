package com.czjt.controller;

import com.czjt.pojo.PageResult;
import com.czjt.pojo.Product;
import com.czjt.pojo.Result;
import com.czjt.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public Result findAll() {
        List<Product> products = productService.findAll();
        return Result.success(products);
    }

    @GetMapping("/{id}")
    public Result findById(@PathVariable Integer id) {
        Product product = productService.findById(id);
        if (product != null) {
            return Result.success(product);
        } else {
            return Result.error("商品不存在");
        }
    }

    @GetMapping("/category/{categoryId}")
    public Result findByCategoryId(@PathVariable Integer categoryId) {
        List<Product> products = productService.findByCategoryId(categoryId);
        return Result.success(products);
    }
    @GetMapping("/name/{name}")
    public Result findByName(@PathVariable String name) {
        List<Product> products = productService.findByName(name);
        return Result.success(products);
    }

    @GetMapping("/page")
    public Result findAllWithPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult pageResult = productService.findAllWithPage(page, pageSize);
        return Result.success(pageResult);
    }

    @GetMapping("/category/{categoryId}/page")
    public Result findByCategoryIdWithPage(
            @PathVariable Integer categoryId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult pageResult = productService.findByCategoryIdWithPage(categoryId, page, pageSize);
        return Result.success(pageResult);
    }
}

