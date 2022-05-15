package com.clint.mybilibili.dao;

import com.clint.mybilibili.domain.UserFollowing;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserFollowingDao {

    Integer removeUserFollowing(@Param("userId") Long userId, @Param("followingId") Long followingId);

    Integer saveUserFollowing(UserFollowing userFollowing);

    List<UserFollowing> getUserFollowings(Long userId);

    List<UserFollowing> getUserFans(Long userId);
}
