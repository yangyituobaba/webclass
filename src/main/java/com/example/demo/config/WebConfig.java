package com.example.demo.config;

import com.example.demo.config.TieredAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private TieredAuthInterceptor tieredAuthInterceptor;

    // 拦截器配置
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tieredAuthInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/user/login",
                        "/user/register",
                        "/uploads/**",
                        "/**/*.html", "/**/*.css", "/**/*.js", "/**/*.png",
                        "/favicon.ico"
                ); // 放行登录、注册、图片、静态资源等
    }

    // 跨域配置
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    // 静态资源映射（例如图片）
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 注意路径最后加 / ！！！
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
