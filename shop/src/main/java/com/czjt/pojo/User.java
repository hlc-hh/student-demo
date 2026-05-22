package com.czjt.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Integer id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private Integer role;
    private Integer status;
    private String createTime;
    private String updateTime;


}
