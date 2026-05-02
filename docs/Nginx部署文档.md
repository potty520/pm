# 拍卖系统 Nginx 部署文档

## 1. 产物信息

- 后端可执行包（Spring Boot Jar）  
  `auction-backend/target/auction-backend-0.0.1-SNAPSHOT.jar`
- 前端构建目录（Vite）  
  `auction-frontend/dist`

## 2. 环境要求

- JDK 17+
- MySQL 8.0+
- Redis 6+
- Nginx 1.18+
- Windows / Linux 均可

## 3. 后端部署

### 3.1 构建 Jar

在 `auction-backend` 目录执行：

```bash
mvn clean package -DskipTests
```

构建完成后产物路径：

```text
auction-backend/target/auction-backend-0.0.1-SNAPSHOT.jar
```

### 3.2 启动后端

在 Jar 所在目录执行：

```bash
java -jar auction-backend-0.0.1-SNAPSHOT.jar
```

默认端口：`8080`

### 3.3 配置文件

后端配置在：

```text
auction-backend/src/main/resources/application.yml
```

重点检查：

- `spring.datasource.*`（MySQL 地址、账号、密码）
- `spring.redis.*`（Redis 地址、密码）
- `server.port`
- `admin.bootstrap.*`（管理员账号自动创建/重置）

## 4. 前端部署

### 4.1 构建前端

在 `auction-frontend` 目录执行：

```bash
npm install
npm run build
```

构建产物：

```text
auction-frontend/dist
```

### 4.2 将 dist 发布到 Nginx 静态目录

例如：

- Linux: `/usr/share/nginx/html/auction`
- Windows: `nginx/html/auction`

## 5. Nginx 配置

可在 `nginx.conf` 或站点配置文件中增加：

```nginx
server {
    listen 80;
    server_name _;

    # 前端静态目录
    root /usr/share/nginx/html/auction;
    index index.html;

    # 前端路由 history 模式
    location / {
        try_files $uri $uri/ /index.html;
    }

    # 后端 API 代理
    location /api/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    # WebSocket 代理
    location /ws {
        proxy_pass http://127.0.0.1:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host $host;
        proxy_read_timeout 86400;
    }
}
```

> 注意：如果前后端分域名部署，请同时配置后端 CORS 白名单。

## 6. 启停与验证

### 6.1 Nginx 重载

```bash
nginx -t
nginx -s reload
```

### 6.2 健康检查

- 前端页面：`http://<your-host>/login`
- 后端接口：`http://<your-host>/api/auction/list`

返回 `code=200` 即表示后端联通正常。

## 7. 常见问题

### 7.1 登录报 `Network Error`

- 检查后端是否启动
- 检查 Nginx `/api` 代理是否正确
- 检查后端 CORS 放行是否包含前端域名

### 7.2 管理员登录失败

检查 `application.yml`：

- `admin.bootstrap.username`
- `admin.bootstrap.password`
- `admin.bootstrap.reset-password`

后端重启后会自动确保管理员用户存在。

