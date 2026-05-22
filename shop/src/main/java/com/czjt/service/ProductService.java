package com.czjt.service;

import com.czjt.mapper.ProductMapper;
import com.czjt.pojo.PageResult;
import com.czjt.pojo.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductMapper productMapper;

    public List<Product> findAll() {
        return productMapper.findAll();
    }

    public List<Product> findByCategoryId(Integer categoryId) {
        return productMapper.findByCategoryId(categoryId);
    }

    public Product findById(Integer id) {
        return productMapper.findById(id);
    }

    public List<Product> findByName(String name) {
        return productMapper.findByName(name);
    }
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
