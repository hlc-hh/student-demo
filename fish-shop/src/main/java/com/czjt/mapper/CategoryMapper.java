package com.czjt.mapper;

import com.czjt.pojo.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {

    @Select("SELECT * FROM category WHERE status = 1 ORDER BY sort ASC, id ASC")
    List<Category> findAll();

    @Select("SELECT * FROM category WHERE id = #{id}")
    Category findById(Long id);

    @Select("SELECT * FROM category WHERE parent_id = #{parentId} AND status = 1 ORDER BY sort ASC, id ASC")
    List<Category> findByParentId(Long parentId);

    @Select("SELECT * FROM category WHERE level = #{level} AND status = 1 ORDER BY sort ASC, id ASC")
    List<Category> findByLevel(Integer level);
}
