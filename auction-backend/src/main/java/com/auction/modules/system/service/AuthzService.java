package com.auction.modules.system.service;

public interface AuthzService {
    boolean isAdmin(Long userId);
    boolean hasPermission(Long userId, String permCode);
}
