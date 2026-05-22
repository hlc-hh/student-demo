package com.czjt.mapper;


import com.czjt.pojo.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductMapper {

    @Select("SELECT * FROM product WHERE status = 1 ORDER BY create_time DESC")
    List<Product> findAll();

    @Select("SELECT * FROM product WHERE category_id = #{categoryId} AND status = 1 ORDER BY create_time DESC")
    List<Product> findByCategoryId(Integer categoryId);

    @Select("SELECT * FROM product WHERE id = #{id}")
    Product findById(Integer id);

    @Select("SELECT * FROM product WHERE name LIKE CONCAT('%', #{name}, '%') AND status = 1 ORDER BY create_time DESC")
    List<Product> findByName(String name);

    @Select("SELECT COUNT(*) FROM product WHERE status = 1")
    Long countAll();

    @Select("SELECT * FROM product WHERE status = 1 ORDER BY create_time DESC LIMIT #{offset}, #{pageSize}")
    List<Product> findAllWithPage(@Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    @Select("SELECT COUNT(*) FROM product WHERE category_id = #{categoryId} AND status = 1")
    Long countByCategoryId(Integer categoryId);

    @Select("SELECT * FROM product WHERE category_id = #{categoryId} AND status = 1 ORDER BY create_time DESC LIMIT #{offset}, #{pageSize}")
    List<Product> findByCategoryIdWithPage(@Param("categoryId") Integer categoryId, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);
}
