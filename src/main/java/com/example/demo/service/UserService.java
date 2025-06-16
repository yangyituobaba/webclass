package com.example.demo.service;

import com.example.demo.bean.Order;
import com.example.demo.bean.User;
import com.example.demo.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;


    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public void register(User user) {
        //检查用户名是否存在
        User existingUser = userDao.findByUsername(user.getUsername());
        if (existingUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.insert(user);
    }
    public void updateUserImage(Integer userId, String imageUrl) {
        userDao.updateUserImage(userId, imageUrl);
    }

    public User login(String username, String rawPassword) {
        User user = userDao.findByUsername(username);
        if (user == null) {
            return null;
        }
        if (passwordEncoder.matches(rawPassword, user.getPassword())) {
            return user;
        } else {
            return null;
        }
    }
    public User getUserInfo(String username){
        return userDao.findByUsername(username);
    }

    public List<User> listAllUsers() {
        return userDao.ListAllUsers();
    }

    public void updateUserInfo(Long userId, String newName, String newUsername, String newPhone, String newPassword) {
        User user = userDao.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (newUsername != null && !newUsername.isEmpty() && !newUsername.equals(user.getUsername())) {
            User existUser = userDao.findByUsername(newUsername);
            if (existUser != null) {
                throw new RuntimeException("用户名已存在，请换一个");
            }
            user.setUsername(newUsername);
        }

        if (newName != null && !newName.isEmpty()) {
            user.setName(newName);
        }

        if (newPhone != null && !newPhone.isEmpty()) {
            user.setPhone(newPhone);
        }

        if (newPassword != null && !newPassword.isEmpty()) {
            // 新密码加密存储
            String encodedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedPassword);
        }

        userDao.update(user);
    }

    public boolean verifyPassword(String username, String rawPassword) {
        User user = userDao.findByUsername(username);
        if (user == null) return false;
        // 使用加密器校验密码
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }



}
