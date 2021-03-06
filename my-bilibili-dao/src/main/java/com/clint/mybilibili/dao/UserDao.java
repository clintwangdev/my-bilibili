package com.clint.mybilibili.dao;

import com.alibaba.fastjson.JSONObject;
import com.clint.mybilibili.domain.RefreshTokenDetail;
import com.clint.mybilibili.domain.User;
import com.clint.mybilibili.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
public interface UserDao {

    /**
     * 根据手机号获取用户
     *
     * @param phone 手机号
     * @return 用户
     */
    User getUserByPhone(String phone);

    /**
     * 添加用户
     */
    Integer saveUser(User user);

    /**
     * 添加用户信息
     */
    Integer saveUserInfo(UserInfo userInfo);

    /**
     * 根据 ID 获取用户
     */
    User getUserById(Long userId);

    /**
     * 根据用户 ID 获取用户信息
     */
    UserInfo getUserInfoByUserId(Long userId);

    /**
     * 修改用户信息
     */
    Integer updateUserInfos(UserInfo userInfo);

    /**
     * 根据用户 ID 批量查询用户信息
     *
     * @param userIdList 关注用户 ID 集合
     * @return 用户信息集合
     */
    List<UserInfo> getUserInfoByUserIds(@Param("userIdList") Set<Long> userIdList);

    /**
     * 获取符合条件的总条数
     */
    Integer pageCountUserInfos(Map params);

    /**
     * 获取用户信息集合
     */
    List<UserInfo> pageListUserInfos(Map params);

    /**
     * 删除 refresh token
     */
    void removeRefreshToken(@Param("userId") Long userId, @Param("refreshToken") String refreshToken);

    /**
     * 添加 refresh token
     */
    void saveRefreshToken(@Param("userId") Long userId,
                          @Param("refreshToken") String refreshToken,
                          @Param("createTime") Date creatTime);

    /**
     * 获取刷新 token
     */
    RefreshTokenDetail getRefreshToken(String refreshToken);

    List<UserInfo> batchGetUserInfosByUserIds(@Param("userIdList") Set<Long> userIdList);
}
