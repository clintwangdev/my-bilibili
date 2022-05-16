package com.clint.mybilibili.dao;

import com.clint.mybilibili.domain.UserMoments;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMomentsDao {

    Integer saveUserMoments(UserMoments userMoments);
}
