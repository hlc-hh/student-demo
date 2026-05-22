package com.czjt.controller;


import com.czjt.pojo.Result;
import com.czjt.pojo.User;
import com.czjt.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result register(@RequestBody User user, HttpServletResponse response) {
        log.info("用户注册:{}", user.getUsername());
        return userService.register(user) ? Result.success("注册成功", null) : Result.error("注册失败");
    }

    @PostMapping("/login")
    public Result login(@RequestBody User user, HttpServletResponse response) {
        log.info("用户登录:{}", user.getUsername());
        try {
            Map<String, Object> data = userService.login(user);
            return Result.success(data);
        } catch (RuntimeException e) {
            log.error("登录失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/info")
    public Result getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String username = (String) request.getAttribute("username");

        if (userId == null) {
            return Result.error("未登录");
        }

        Map<String, Object> userInfo = Map.of(
            "userId", userId,
            "username", username
        );
        return Result.success(userInfo);
    }

    @PostMapping("/logout")
    public Result logout() {
        return Result.success("退出成功", null);
    }
}
