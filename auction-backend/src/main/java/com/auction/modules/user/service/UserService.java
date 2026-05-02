package com.auction.modules.user.service;

import com.auction.modules.user.dto.*;

import java.math.BigDecimal;
import java.util.List;

public interface UserService {
    UserVO register(UserRegisterRequest request);

    UserLoginResponse login(UserLoginRequest request);

    UserVO getById(Long userId);

    UserVO updateProfile(Long userId, UserProfileUpdateRequest request);
    UserVO updateUserByAdmin(Long userId, UserAdminUpdateRequest request);

    UserVO verifyRealName(Long userId, UserVerifyRequest request);

    UserVO addDeposit(Long userId, BigDecimal amount);

    List<UserVO> listUsers();

    void deleteUser(Long userId);
}
