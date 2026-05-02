package com.auction;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan({
        "com.auction.modules.auction.mapper",
        "com.auction.modules.order.mapper",
        "com.auction.modules.user.mapper",
        "com.auction.modules.category.mapper",
        "com.auction.modules.session.mapper",
        "com.auction.modules.system.mapper",
        "com.auction.modules.log.mapper"
})
public class AuctionBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuctionBackendApplication.class, args);
    }
}
