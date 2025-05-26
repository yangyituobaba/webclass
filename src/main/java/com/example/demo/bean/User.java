package com.example.demo.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Integer id;
    private String name;    //用户姓名
    private String username;    //用户登录名
    private String password;
    private String phone;
    private String role;    //用户角色

    private String token;   //登录成功后的JWT token
    private Boolean isActive;   //用户是否启用
}
