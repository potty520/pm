package com.auction.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchemaPatchRunner implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        ensureTables();
        patchAuctionUser();
        patchAuctionItem();
        seedPermissions();
    }

    private void ensureTables() {
        execIgnore("CREATE TABLE IF NOT EXISTS auction_category (id BIGINT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(100) NOT NULL, parent_id BIGINT DEFAULT 0, sort INT DEFAULT 0, status TINYINT DEFAULT 1, created_at DATETIME DEFAULT CURRENT_TIMESTAMP, updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)");
        execIgnore("CREATE TABLE IF NOT EXISTS auction_session (id BIGINT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(100) NOT NULL, description VARCHAR(255), start_time DATETIME, end_time DATETIME, status TINYINT DEFAULT 1, created_at DATETIME DEFAULT CURRENT_TIMESTAMP, updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)");
        execIgnore("CREATE TABLE IF NOT EXISTS sys_role (id BIGINT PRIMARY KEY AUTO_INCREMENT, role_code VARCHAR(50) NOT NULL UNIQUE, role_name VARCHAR(100) NOT NULL, status TINYINT DEFAULT 1)");
        execIgnore("CREATE TABLE IF NOT EXISTS sys_permission (id BIGINT PRIMARY KEY AUTO_INCREMENT, perm_code VARCHAR(100) NOT NULL UNIQUE, perm_name VARCHAR(100) NOT NULL)");
        execIgnore("CREATE TABLE IF NOT EXISTS sys_user_role (id BIGINT PRIMARY KEY AUTO_INCREMENT, user_id BIGINT NOT NULL, role_id BIGINT NOT NULL, UNIQUE KEY uk_user_role (user_id, role_id))");
        execIgnore("CREATE TABLE IF NOT EXISTS sys_role_permission (id BIGINT PRIMARY KEY AUTO_INCREMENT, role_id BIGINT NOT NULL, permission_id BIGINT NOT NULL, UNIQUE KEY uk_role_perm (role_id, permission_id))");
        execIgnore("CREATE TABLE IF NOT EXISTS op_log (id BIGINT PRIMARY KEY AUTO_INCREMENT, user_id BIGINT, action VARCHAR(100), target_type VARCHAR(50), target_id BIGINT, detail VARCHAR(255), success TINYINT DEFAULT 1, created_at DATETIME DEFAULT CURRENT_TIMESTAMP)");
    }

    private void patchAuctionUser() {
        execIgnore("ALTER TABLE auction_user ADD COLUMN real_name VARCHAR(50) NULL");
        execIgnore("ALTER TABLE auction_user ADD COLUMN id_card VARCHAR(18) NULL");
        execIgnore("ALTER TABLE auction_user ADD COLUMN auth_status TINYINT DEFAULT 0");
        execIgnore("ALTER TABLE auction_user ADD COLUMN deposit DECIMAL(10,2) DEFAULT 0");
    }

    private void patchAuctionItem() {
        execIgnore("ALTER TABLE auction_item ADD COLUMN reject_reason VARCHAR(255) NULL");
        execIgnore("ALTER TABLE auction_item ADD COLUMN session_id BIGINT NULL");
    }

    private void seedPermissions() {
        execIgnore("INSERT IGNORE INTO sys_role (role_code, role_name, status) VALUES ('buyer', '买家', 1)");
        execIgnore("INSERT IGNORE INTO sys_role (role_code, role_name, status) VALUES ('seller', '卖家', 1)");
        execIgnore("INSERT IGNORE INTO sys_permission (perm_code, perm_name) VALUES ('admin:*', '超级管理员')");
        execIgnore("INSERT IGNORE INTO sys_permission (perm_code, perm_name) VALUES ('admin:stats', '查看统计')");
        execIgnore("INSERT IGNORE INTO sys_permission (perm_code, perm_name) VALUES ('admin:item:list', '查看拍品')");
        execIgnore("INSERT IGNORE INTO sys_permission (perm_code, perm_name) VALUES ('admin:item:review', '拍品审核')");
        execIgnore("INSERT IGNORE INTO sys_permission (perm_code, perm_name) VALUES ('admin:order:list', '查看订单')");
        execIgnore("INSERT IGNORE INTO sys_permission (perm_code, perm_name) VALUES ('admin:user:list', '查看用户')");
        execIgnore("INSERT IGNORE INTO sys_permission (perm_code, perm_name) VALUES ('admin:user:update', '编辑用户')");
        execIgnore("INSERT IGNORE INTO sys_permission (perm_code, perm_name) VALUES ('admin:user:delete', '删除用户')");
        execIgnore("INSERT IGNORE INTO sys_permission (perm_code, perm_name) VALUES ('admin:category:manage', '分类管理')");
        execIgnore("INSERT IGNORE INTO sys_permission (perm_code, perm_name) VALUES ('admin:session:manage', '专场管理')");
        execIgnore("INSERT IGNORE INTO sys_permission (perm_code, perm_name) VALUES ('admin:log:view', '日志查看')");
    }

    private void execIgnore(String sql) {
        try {
            jdbcTemplate.execute(sql);
            log.info("Applied schema patch: {}", sql);
        } catch (Exception ex) {
            // Ignore when column already exists or table not ready.
            log.debug("Skip schema patch: {} - {}", sql, ex.getMessage());
        }
    }
}
