package com.auction.modules.user.controller;

import com.auction.common.result.Result;
import com.auction.common.utils.SecurityUtils;
import com.auction.modules.log.service.OpLogService;
import com.auction.modules.system.service.AuthzService;
import com.auction.modules.user.dto.*;
import com.auction.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;
    private final AuthzService authzService;
    private final OpLogService opLogService;

    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody UserRegisterRequest request) {
        UserVO user = userService.register(request);
        opLogService.log(user.getId(), "REGISTER", "USER", user.getId(), user.getUsername(), true);
        return Result.ok(user);
    }

    @PostMapping("/login")
    public Result<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest request) {
        UserLoginResponse response = userService.login(request);
        opLogService.log(response.getUserId(), "LOGIN", "USER", response.getUserId(), response.getUsername(), true);
        return Result.ok(response);
    }

    @GetMapping("/me")
    public Result<UserVO> me() {
        Long currentUserId = SecurityUtils.currentUserId();
        if (currentUserId == null) {
            return Result.fail("未登录");
        }
        return Result.ok(userService.getById(currentUserId));
    }

    @PutMapping("/me")
    public Result<UserVO> updateMe(@RequestBody UserProfileUpdateRequest request) {
        Long currentUserId = SecurityUtils.currentUserId();
        if (currentUserId == null) {
            return Result.fail("未登录");
        }
        return Result.ok(userService.updateProfile(currentUserId, request));
    }

    @PostMapping("/verify")
    public Result<UserVO> verify(@Valid @RequestBody UserVerifyRequest request) {
        Long currentUserId = SecurityUtils.currentUserId();
        if (currentUserId == null) {
            return Result.fail("未登录");
        }
        UserVO user = userService.verifyRealName(currentUserId, request);
        opLogService.log(currentUserId, "VERIFY_REAL_NAME", "USER", currentUserId, request.getRealName(), true);
        return Result.ok(user);
    }

    @PostMapping("/deposit")
    public Result<UserVO> deposit(@Valid @RequestBody UserDepositRequest request) {
        Long currentUserId = SecurityUtils.currentUserId();
        if (currentUserId == null) {
            return Result.fail("未登录");
        }
        UserVO user = userService.addDeposit(currentUserId, request.getAmount());
        opLogService.log(currentUserId, "ADD_DEPOSIT", "USER", currentUserId, request.getAmount().toPlainString(), true);
        return Result.ok(user);
    }

    @GetMapping("/list")
    public Result<List<UserVO>> list() {
        Long uid = SecurityUtils.currentUserId();
        if (!authzService.hasPermission(uid, "admin:user:list")) {
            return Result.fail("无管理员权限");
        }
        return Result.ok(userService.listUsers());
    }

    @PutMapping("/{userId}")
    public Result<UserVO> updateUser(@PathVariable Long userId, @RequestBody UserAdminUpdateRequest request) {
        Long uid = SecurityUtils.currentUserId();
        if (!authzService.hasPermission(uid, "admin:user:update")) {
            return Result.fail("无管理员权限");
        }
        UserVO data = userService.updateUserByAdmin(userId, request);
        opLogService.log(uid, "UPDATE_USER", "USER", userId, "", true);
        return Result.ok(data);
    }

    @DeleteMapping("/{userId}")
    public Result<Void> delete(@PathVariable Long userId) {
        Long currentUserId = SecurityUtils.currentUserId();
        if (currentUserId == null) {
            return Result.fail("未登录");
        }
        if (!authzService.hasPermission(currentUserId, "admin:user:delete") && !currentUserId.equals(userId)) {
            return Result.fail("无权限删除该用户");
        }
        userService.deleteUser(userId);
        opLogService.log(currentUserId, "DELETE_USER", "USER", userId, "", true);
        return Result.ok(null);
    }
}
