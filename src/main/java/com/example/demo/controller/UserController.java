package com.example.demo.controller;

import cn.hutool.json.JSONUtil;
import com.example.demo.annotation.AccessVerification;
import com.example.demo.annotation.VerificationLevel;
import com.example.demo.bean.Order;
import com.example.demo.bean.Result;
import com.example.demo.bean.User;
import com.example.demo.service.UserService;
import com.example.demo.utils.RedisConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 注册接口（不需要认证）
     */
    @AccessVerification(VerificationLevel.NONE)
    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        try {
            userService.register(user);
            return Result.success("注册成功");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 登录接口（不需要认证）
     */
    @AccessVerification(VerificationLevel.NONE)
    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> request,
                        @RequestHeader(value = "Device-Type", defaultValue = "WEB") String deviceType) {
        String username = request.get("username");
        String password = request.get("password");

        User user = userService.login(username, password);
        if (user == null) return Result.fail("用户名或密码错误");

        // 生成 token
        String token = UUID.randomUUID().toString();

        // Redis key 定义
        String redisKey = RedisConstants.LOGIN_TOKEN_KEY + token;
        String userKey = RedisConstants.LOGIN_USER_TOKEN_PREFIX + user.getId() + ":" + deviceType;

        // 避免存储密码
        user.setPassword(null);
        user.setToken(token);

        // 保存用户信息到 Redis
        String json = JSONUtil.toJsonStr(user);
        redisTemplate.opsForValue().set(redisKey, json, RedisConstants.LOGIN_TTL, TimeUnit.HOURS);
        redisTemplate.opsForValue().set(userKey, token, RedisConstants.LOGIN_TTL, TimeUnit.HOURS);

        return Result.success(user);
    }

    /**
     * 获取当前登录用户的信息（需要一级认证）
     */
    @PostMapping("/info")
    @AccessVerification(VerificationLevel.PRIMARY)
    public Result info(HttpServletRequest request) {
        User user = (User) request.getAttribute("currentUser");
        if (user == null) {
            return Result.fail("用户未登录");
        }
        return Result.success(userService.getUserInfo(user.getUsername()));
    }

    // 上传用户头像
    @AccessVerification(VerificationLevel.PRIMARY)
    @PostMapping("/upimage")
    public Result<String> uploadUserAvatar(@RequestParam("file") MultipartFile file,
                                           HttpServletRequest request) {
        if (file.isEmpty()) {
            return Result.fail("上传文件不能为空");
        }

        // 获取当前登录用户（由拦截器或过滤器注入）
        User currentUser = (User) request.getAttribute("currentUser");
        if (currentUser == null) {
            return Result.fail("未登录");
        }

        try {
            // 保存路径
            String uploadDir = "uploads/avatars/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 新文件名（UUID + 原文件名防止冲突）
            String originalFilename = file.getOriginalFilename();
            String newFilename = UUID.randomUUID().toString() + "-" + originalFilename;
            File dest = new File(uploadDir + newFilename);
            file.transferTo(dest);
            String baseUrl = "http://localhost:8000/uploads/";

            // 产品图片（上传到 uploads/products/）
            String imageUrl = baseUrl + "avatars/" + newFilename;

            // 更新数据库中的 image_url 字段
            userService.updateUserImage(currentUser.getId(), imageUrl);

            return Result.success(imageUrl);
        } catch (IOException e) {
            return Result.fail("头像上传失败：" + e.getMessage());
        }
    }

    // 获取所有用户（仅限管理员）
    @AccessVerification(VerificationLevel.PRIMARY)
    @GetMapping("/all")
    public Result listAllUsers() {
        List<User> users = userService.listAllUsers();
        return Result.success(users);
    }

    @AccessVerification(VerificationLevel.PRIMARY) // 只验证 Token
    @PutMapping("/update")
    public Result updateUserInfo(@RequestBody Map<String, String> params,
                                 HttpServletRequest request) {

        User currentUser = (User) request.getAttribute("currentUser");
        if (currentUser == null) {
            return Result.fail("用户未登录");
        }

        String currentPassword = params.get("currentPassword");
        if (currentPassword == null || currentPassword.isEmpty()) {
            return Result.fail("需要提供当前密码进行验证");
        }

        boolean valid = userService.verifyPassword(currentUser.getUsername(), currentPassword);
        if (!valid) {
            return Result.fail("当前密码错误");
        }

        String newName = params.get("name");
        String newUsername = params.get("username");
        String newPhone = params.get("phone");
        String newPassword = params.get("password");

        try {
            userService.updateUserInfo(currentUser.getId().longValue(), newName, newUsername, newPhone, newPassword);
            return Result.success("个人信息更新成功");
        } catch (Exception e) {
            return Result.fail("更新失败：" + e.getMessage());
        }
    }


}
