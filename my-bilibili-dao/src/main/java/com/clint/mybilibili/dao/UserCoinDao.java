package com.clint.mybilibili.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Mapper
public interface UserCoinDao {

    Long getUserCoinAmount(Long userId);

    Integer updateUserCoinAmount(@Param("userId") Long userId, @Param("amount") Long amount, @Param("updateTime") Date updateTime);
}
