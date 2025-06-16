package com.example.demo.dao;

import com.example.demo.bean.Order;
import com.example.demo.bean.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserDao {

    //username转化为userID
    @Select("SELECT id FROM user WHERE username = #{username}")
    Integer selectIdByUsername(String username);

    @Update("UPDATE user SET image_url = #{imageUrl} WHERE id = #{userId}")
    void updateUserImage(@Param("userId") Integer userId, @Param("imageUrl") String imageUrl);

    // 根据用户名查找用户（注册时用于查重，登录时用于验证）
    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    // 插入新用户
    @Insert("INSERT INTO user (name, username, password, phone, role) " +
            "VALUES (#{name}, #{username}, #{password}, #{phone}, #{role})")
    void insert(User user);

    //获取全部用户
    @Select("SELECT * FROM user ")
    List<User> ListAllUsers();

    // 根据ID查找用户
    @Select("SELECT * FROM user WHERE id = #{userId}")
    User findById(@Param("userId") Long userId);

    // 更新用户信息
    @Update("UPDATE user SET name = #{name}, username = #{username}, password = #{password}, phone = #{phone} WHERE id = #{id}")
    void update(User user);
}
