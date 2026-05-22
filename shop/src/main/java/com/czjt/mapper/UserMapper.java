package com.czjt.mapper;

import com.czjt.pojo.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(String username);

    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Integer id);

    @Insert("INSERT INTO user(username, password, email, phone, role, status, create_time, update_time) " +
            "VALUES(#{username}, #{password}, #{email}, #{phone}, #{role}, #{status}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE user SET password=#{password}, email=#{email}, phone=#{phone}, update_time=#{updateTime} WHERE id=#{id}")
    int update(User user);
}
