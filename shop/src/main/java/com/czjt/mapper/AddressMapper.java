package com.czjt.mapper;

import com.czjt.pojo.Address;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AddressMapper {

    List<Address> findByUserId(@Param("userId") Long userId);

    Address findById(@Param("id") Long id);

    Address findDefaultByUserId(@Param("userId") Long userId);

    int insert(Address address);

    int update(Address address);

    int deleteById(@Param("id") Long id, @Param("userId") Long userId);

    int setDefault(@Param("id") Long id, @Param("userId") Long userId);

    int cancelDefault(@Param("userId") Long userId);
}
