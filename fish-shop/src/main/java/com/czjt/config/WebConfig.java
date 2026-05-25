package com.czjt.config;

import com.czjt.interceptor.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private TokenInterceptor tokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/cart/**")
                .addPathPatterns("/order/**")
                .excludePathPatterns("/user/login")
                .excludePathPatterns("/register")
                .addPathPatterns("/orders/**")      // 添加订单路径
                .addPathPatterns("/addresses/**")   // 添加地址路径
                .excludePathPatterns("/products/**")
                .excludePathPatterns("/categories/**")
                .excludePathPatterns("/**/*.html")
                .excludePathPatterns("/**/*.css")
                .excludePathPatterns("/**/*.js")
                .excludePathPatterns("/**/*.png")
                .excludePathPatterns("/**/*.jpg")
                .excludePathPatterns("/**/*.gif");
    }
}
