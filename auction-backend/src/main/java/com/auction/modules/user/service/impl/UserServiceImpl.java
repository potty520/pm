package com.auction.modules.user.service.impl;

import com.auction.modules.user.dto.*;
import com.auction.modules.user.entity.AuctionUser;
import com.auction.modules.user.mapper.AuctionUserMapper;
import com.auction.modules.user.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AuctionUserMapper auctionUserMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO register(UserRegisterRequest request) {
        AuctionUser existing = auctionUserMapper.selectOne(
                new LambdaQueryWrapper<AuctionUser>().eq(AuctionUser::getUsername, request.getUsername()).last("LIMIT 1")
        );
        if (existing != null) {
            throw new IllegalStateException("用户名已存在");
        }

        AuctionUser user = new AuctionUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setStatus(1);
        user.setAuthStatus(0);
        user.setDeposit(BigDecimal.ZERO);
        auctionUserMapper.insert(user);
        return toVO(user);
    }

    @Override
    public UserLoginResponse login(UserLoginRequest request) {
        AuctionUser user = auctionUserMapper.selectOne(
                new LambdaQueryWrapper<AuctionUser>().eq(AuctionUser::getUsername, request.getUsername()).last("LIMIT 1")
        );
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new IllegalStateException("用户已被禁用");
        }
        return new UserLoginResponse(user.getId(), user.getUsername(), "uid:" + user.getId());
    }

    @Override
    public UserVO getById(Long userId) {
        AuctionUser user = mustGetUser(userId);
        return toVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO updateProfile(Long userId, UserProfileUpdateRequest request) {
        AuctionUser user = mustGetUser(userId);
        user.setNickname(request.getNickname());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        auctionUserMapper.updateById(user);
        return toVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO updateUserByAdmin(Long userId, UserAdminUpdateRequest request) {
        AuctionUser user = mustGetUser(userId);
        user.setNickname(request.getNickname());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        if (request.getAuthStatus() != null) {
            user.setAuthStatus(request.getAuthStatus());
        }
        if (request.getDeposit() != null) {
            user.setDeposit(request.getDeposit());
        }
        auctionUserMapper.updateById(user);
        return toVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO verifyRealName(Long userId, UserVerifyRequest request) {
        AuctionUser user = mustGetUser(userId);
        user.setRealName(request.getRealName());
        user.setIdCard(request.getIdCard());
        user.setAuthStatus(1);
        auctionUserMapper.updateById(user);
        return toVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO addDeposit(Long userId, BigDecimal amount) {
        AuctionUser user = mustGetUser(userId);
        BigDecimal current = user.getDeposit() == null ? BigDecimal.ZERO : user.getDeposit();
        user.setDeposit(current.add(amount));
        auctionUserMapper.updateById(user);
        return toVO(user);
    }

    @Override
    public List<UserVO> listUsers() {
        return auctionUserMapper.selectList(
                new LambdaQueryWrapper<AuctionUser>().orderByDesc(AuctionUser::getId)
        ).stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long userId) {
        mustGetUser(userId);
        auctionUserMapper.deleteById(userId);
    }

    private AuctionUser mustGetUser(Long userId) {
        AuctionUser user = auctionUserMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        return user;
    }

    private UserVO toVO(AuctionUser user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setRealName(user.getRealName());
        vo.setIdCard(user.getIdCard());
        vo.setAuthStatus(user.getAuthStatus());
        vo.setDeposit(user.getDeposit());
        vo.setStatus(user.getStatus());
        vo.setCreatedAt(user.getCreatedAt());
        return vo;
    }
}
