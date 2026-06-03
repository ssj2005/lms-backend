package com.lms.modules.user.controller;

import com.lms.common.exception.BusinessException;
import com.lms.common.result.Result;
import com.lms.modules.user.entity.User;
import com.lms.modules.user.service.UserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserInfoController {

    @Autowired
    private UserService userService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping("/info")
    public Result<Map<String, Object>> getCurrentUserInfo() {
        Long userId = getCurrentUserId();
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return Result.success(buildUserInfo(user));
    }

    @PutMapping("/info")
    public Result<Map<String, Object>> updateCurrentUserInfo(@RequestBody UpdateInfoRequest request) {
        Long userId = getCurrentUserId();
        User user = new User();
        user.setId(userId);
        user.setRealName(request.getRealName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        userService.updateById(user);

        User updatedUser = userService.getById(userId);
        return Result.success(buildUserInfo(updatedUser));
    }

    @PostMapping("/change-password")
    public Result<Void> changePassword(@RequestBody ChangePasswordRequest request) {
        Long userId = getCurrentUserId();
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("当前密码错误");
        }

        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setPassword(request.getNewPassword());
        userService.updateById(updateUser);

        return Result.success();
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new BusinessException("未登录");
        }
        return (Long) authentication.getPrincipal();
    }

    private Map<String, Object> buildUserInfo(User user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("realName", user.getRealName());
        userInfo.put("phone", user.getPhone());
        userInfo.put("email", user.getEmail());
        userInfo.put("role", user.getRole());
        return userInfo;
    }

    @Data
    public static class UpdateInfoRequest {
        private String realName;
        private String email;
        private String phone;
    }

    @Data
    public static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;
    }
}