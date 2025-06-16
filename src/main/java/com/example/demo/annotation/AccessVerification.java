package com.example.demo.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


//接口访问级别注解

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME )
public @interface AccessVerification {
    //验证级别：默认为一级验证
    VerificationLevel value() default VerificationLevel.NONE;
}
