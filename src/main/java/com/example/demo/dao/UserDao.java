package com.example.demo.dao;

import com.example.demo.bean.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao {
    User findByUsername(String username);
    void insertUser(User user);
    User findById(int id);
    void updateUser(User user);
}
