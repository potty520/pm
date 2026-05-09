CREATE TABLE IF NOT EXISTS auction_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(200) NOT NULL,
  description TEXT,
  category_id INT,
  session_id BIGINT,
  start_price DECIMAL(10,2) NOT NULL,
  reserve_price DECIMAL(10,2),
  current_price DECIMAL(10,2),
  bid_increment DECIMAL(10,2) DEFAULT 100,
  seller_id BIGINT NOT NULL,
  status TINYINT DEFAULT 0,
  reject_reason VARCHAR(255),
  start_time DATETIME,
  end_time DATETIME,
  view_count INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS bid_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  item_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  bid_amount DECIMAL(10,2) NOT NULL,
  bid_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  is_win TINYINT DEFAULT 0,
  ip_address VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS auction_order (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_no VARCHAR(32) NOT NULL UNIQUE,
  item_id BIGINT NOT NULL,
  buyer_id BIGINT NOT NULL,
  seller_id BIGINT NOT NULL,
  final_price DECIMAL(10,2) NOT NULL,
  status TINYINT DEFAULT 0,
  payment_method VARCHAR(20),
  payment_time DATETIME,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS auction_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  nickname VARCHAR(50),
  phone VARCHAR(20),
  email VARCHAR(100),
  real_name VARCHAR(50),
  id_card VARCHAR(18),
  auth_status TINYINT DEFAULT 0,
  deposit DECIMAL(10,2) DEFAULT 0,
  status TINYINT DEFAULT 1,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS auction_category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  parent_id BIGINT DEFAULT 0,
  sort INT DEFAULT 0,
  status TINYINT DEFAULT 1,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS auction_session (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(255),
  start_time DATETIME,
  end_time DATETIME,
  status TINYINT DEFAULT 1,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sys_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_code VARCHAR(50) NOT NULL UNIQUE,
  role_name VARCHAR(100) NOT NULL,
  status TINYINT DEFAULT 1
);

CREATE TABLE IF NOT EXISTS sys_permission (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  perm_code VARCHAR(100) NOT NULL UNIQUE,
  perm_name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS sys_user_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  UNIQUE KEY uk_user_role (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS sys_role_permission (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_id BIGINT NOT NULL,
  permission_id BIGINT NOT NULL,
  UNIQUE KEY uk_role_perm (role_id, permission_id)
);

CREATE TABLE IF NOT EXISTS op_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT,
  action VARCHAR(100),
  target_type VARCHAR(50),
  target_id BIGINT,
  detail VARCHAR(255),
  success TINYINT DEFAULT 1,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for foreign keys and frequent queries
CREATE INDEX idx_item_status ON auction_item(status);
CREATE INDEX idx_item_seller ON auction_item(seller_id);
CREATE INDEX idx_item_category ON auction_item(category_id);
CREATE INDEX idx_item_session ON auction_item(session_id);
CREATE INDEX idx_item_start_time ON auction_item(start_time);

CREATE INDEX idx_bid_item ON bid_record(item_id);
CREATE INDEX idx_bid_user ON bid_record(user_id);
CREATE INDEX idx_bid_time ON bid_record(bid_time);

CREATE INDEX idx_order_buyer ON auction_order(buyer_id);
CREATE INDEX idx_order_seller ON auction_order(seller_id);
CREATE INDEX idx_order_item ON auction_order(item_id);
CREATE INDEX idx_order_status ON auction_order(status);

CREATE INDEX idx_user_status ON auction_user(status);
CREATE INDEX idx_user_username ON auction_user(username);

CREATE INDEX idx_log_user ON op_log(user_id);
CREATE INDEX idx_log_created ON op_log(created_at);
