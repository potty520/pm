package com.auction.modules.user.mapper;

import com.auction.modules.user.entity.AuctionUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuctionUserMapper extends BaseMapper<AuctionUser> {
}
