package com.lms.modules.user.controller;

import com.lms.common.result.Result;
import com.lms.modules.user.service.AuthService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest request) {
        log.info("用户登录: {}", request.getUsername());
        Map<String, Object> result = authService.login(request.getUsername(), request.getPassword());
        return Result.success(result);
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }
}
