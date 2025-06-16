package com.example.demo.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    //客户实体类
    private Integer id;
    private String name;    //用户姓名
    private String username;    //用户登录名
    private String password;
    private String phone;
    private String role;    //用户角色
    private String image;       // 用户头像URL
    private String token;   //登录成功后的token
}
