package com.example.demo.utils;

public class RedisConstants {
    public static final Long LOGIN_TTL = 2L; // 登录有效期 2 小时
    public static final String LOGIN_TOKEN_KEY = "token:";
    public static final String LOGIN_USER_TOKEN_PREFIX = "login:token:";
}
