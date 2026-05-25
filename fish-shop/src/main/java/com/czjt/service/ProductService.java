package com.czjt.service;

import com.czjt.mapper.ProductMapper;
import com.czjt.pojo.PageResult;
import com.czjt.pojo.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Cacheable(value = "product:all", unless = "#result == null || #result.isEmpty()")
    public List<Product> findAll() {
        return productMapper.findAll();
    }

    @Cacheable(value = "product:category", key = "#categoryId", unless = "#result == null || #result.isEmpty()")
    public List<Product> findByCategoryId(Integer categoryId) {
        return productMapper.findByCategoryId(categoryId);
    }

    @Cacheable(value = "product:id", key = "#id", unless = "#result == null")
    public Product findById(Integer id) {
        return productMapper.findById(id);
    }

    public List<Product> findByName(String name) {
        return productMapper.findByName(name);
    }

    @Cacheable(value = "product:page", key = "#page + '-' + #pageSize", unless = "#result == null || #result.items == null || #result.items.isEmpty()")
    public PageResult findAllWithPage(Integer page, Integer pageSize) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        Integer offset = (page - 1) * pageSize;
        Long total = productMapper.countAll();
        List<Product> items = productMapper.findAllWithPage(offset, pageSize);

        PageResult pageResult = new PageResult();
        pageResult.setTotal(total);
        pageResult.setItems(items);

        return pageResult;
    }

    @Cacheable(value = "product:categoryPage", key = "#categoryId + '-' + #page + '-' + #pageSize", unless = "#result == null || #result.items == null || #result.items.isEmpty()")
    public PageResult findByCategoryIdWithPage(Integer categoryId, Integer page, Integer pageSize) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        Integer offset = (page - 1) * pageSize;
        Long total = productMapper.countByCategoryId(categoryId);
        List<Product> items = productMapper.findByCategoryIdWithPage(categoryId, offset, pageSize);

        PageResult pageResult = new PageResult();
        pageResult.setTotal(total);
        pageResult.setItems(items);

        return pageResult;
    }
}
