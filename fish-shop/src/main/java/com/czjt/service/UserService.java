package com.czjt.service;

import com.czjt.mapper.UserMapper;
import com.czjt.pojo.User;
import com.czjt.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public boolean register(User user) {
        log.info("开始注册用户: {}", user.getUsername());

        try {
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                log.warn("用户名不能为空");
                return false;
            }

            if (user.getPassword() == null || user.getPassword().length() < 6) {
                log.warn("密码长度不能少于6位");
                return false;
            }

            User existingUser = userMapper.findByUsername(user.getUsername());
            if (existingUser != null) {
                log.warn("用户名已存在: {}", user.getUsername());
                return false;
            }

            // 设置默认值
            if (user.getRole() == null) {
                user.setRole(0);
            }
            user.setStatus(0);  // 0表示正常，1表示禁用
            if (user.getEmail() == null || user.getEmail().isEmpty()) {
                user.setEmail(user.getUsername() + "@example.com");
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String now = LocalDateTime.now().format(formatter);
            user.setCreateTime(now);
            user.setUpdateTime(now);

            int result = userMapper.insert(user);
            log.info("用户注册结果: {}, 影响行数: {}", user.getUsername(), result);
            return result > 0;
        } catch (Exception e) {
            log.error("注册失败，错误信息: {}", e.getMessage(), e);
            return false;
        }
    }
    public Map<String, Object> login(User user) {
        log.info("用户登录: {}", user.getUsername());

        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new RuntimeException("密码不能为空");
        }

        User dbUser = userMapper.findByUsername(user.getUsername());
        if (dbUser == null) {
            log.warn("用户不存在: {}", user.getUsername());
            throw new RuntimeException("用户名或密码错误");
        }

        // 检查状态：0=正常，1=禁用
        if (dbUser.getStatus() != null && dbUser.getStatus() != 0) {
            log.warn("账号状态异常: {}, 状态: {}", user.getUsername(), dbUser.getStatus());
            throw new RuntimeException("账号已被禁用，请联系管理员");
        }

        if (!user.getPassword().equals(dbUser.getPassword())) {
            log.warn("密码错误: {}", user.getUsername());
            throw new RuntimeException("用户名或密码错误");
        }

        String token = JwtUtil.generateToken(dbUser.getUsername(), dbUser.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", dbUser.getId());
        result.put("username", dbUser.getUsername());
        result.put("role", dbUser.getRole() != null ? dbUser.getRole() : 0);
        result.put("roleName", (dbUser.getRole() != null && dbUser.getRole() == 1) ? "管理员" : "普通用户");
        result.put("email", dbUser.getEmail());
        result.put("phone", dbUser.getPhone());

        log.info("用户登录成功: {}, 角色: {}", user.getUsername(),
                (dbUser.getRole() != null && dbUser.getRole() == 1) ? "管理员" : "普通用户");
        return result;
    }
}
