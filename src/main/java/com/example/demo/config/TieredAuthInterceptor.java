package com.example.demo.config;

import cn.hutool.json.JSONUtil;
import com.example.demo.annotation.AccessVerification;
import com.example.demo.annotation.VerificationLevel;
import com.example.demo.bean.Result;
import com.example.demo.bean.User;
import com.example.demo.utils.RedisConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class TieredAuthInterceptor implements HandlerInterceptor {
    //拦截
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // 1. 放行预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
            response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Authorization,Verification-Code,Content-Type");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            return false;  // 预检请求不继续执行后续逻辑
        }

        VerificationLevel requiredLevel = getRequiredVerificationLevel(handler);

        if (requiredLevel == VerificationLevel.NONE) {
            return true;
        }

        User user;

        if (requiredLevel == VerificationLevel.PRIMARY) {
            user = verifyTokenOnly(request);
        } else { // SECONDARY
            user = verifyTokenAndPassword(request);
        }

        if (user == null) {
            sendError(response, 401, "身份验证失败/");
            return false;
        }

        request.setAttribute("currentUser", user);
        return true;
    }


    private VerificationLevel getRequiredVerificationLevel(Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            // 方法上注解优先
            AccessVerification methodAnnotation =
                    handlerMethod.getMethodAnnotation(AccessVerification.class);
            if (methodAnnotation != null) {
                return methodAnnotation.value();
            }

            // 类上注解次之
            AccessVerification classAnnotation =
                    handlerMethod.getBeanType().getAnnotation(AccessVerification.class);
            if (classAnnotation != null) {
                return classAnnotation.value();
            }
        }
        // 默认需要一级验证
        return VerificationLevel.PRIMARY;
    }

    private User verifyTokenOnly(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (!StringUtils.hasText(token)) {
            return null;
        }

        String userJson = redisTemplate.opsForValue().get(RedisConstants.LOGIN_TOKEN_KEY + token);
        if (!StringUtils.hasText(userJson)) {
            return null;
        }

        // 刷新 Token 的有效期
        redisTemplate.expire(RedisConstants.LOGIN_TOKEN_KEY + token, RedisConstants.LOGIN_TTL, TimeUnit.HOURS);

        return JSONUtil.toBean(userJson, User.class);
    }

    private User verifyTokenAndPassword(HttpServletRequest request) {
        User user = verifyTokenOnly(request);
        if (user == null) return null;
        System.out.println("Redis里密码: " + user.getPassword());

        String password = request.getHeader("Verification-Code");
        if (!StringUtils.hasText(password) || !password.equals(user.getPassword())) {
            return null;
        }
        return user;
    }


    private void sendError(HttpServletResponse response, int code, String msg) throws IOException {
        response.setStatus(code);
        response.setContentType("application/json");
        response.getWriter().write(JSONUtil.toJsonStr(Result.fail(msg)));
    }
}