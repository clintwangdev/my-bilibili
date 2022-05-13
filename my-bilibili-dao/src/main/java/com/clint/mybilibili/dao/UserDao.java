package com.clint.mybilibili.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao {

    Long query(Long id);
}
