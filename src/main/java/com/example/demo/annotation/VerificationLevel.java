package com.example.demo.annotation;

public enum VerificationLevel {
    /**
     * 不需要验证 - 公开接口
     */
    NONE,

    /**
     * 一级验证 - 基础身份验证（登录验证）
     */
    PRIMARY,

    /**
     * 二级验证 - 敏感操作需要额外验证
     */
    SECONDARY
}
