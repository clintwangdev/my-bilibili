package com.clint.mybilibili.service;

import com.alibaba.fastjson.JSONObject;
import com.clint.mybilibili.dao.UserDao;
import com.clint.mybilibili.domain.PageResult;
import com.clint.mybilibili.domain.RefreshTokenDetail;
import com.clint.mybilibili.domain.User;
import com.clint.mybilibili.domain.UserInfo;
import com.clint.mybilibili.domain.auth.UserRole;
import com.clint.mybilibili.domain.constant.UserConstant;
import com.clint.mybilibili.domain.exception.ConditionException;
import com.clint.mybilibili.service.util.MD5Util;
import com.clint.mybilibili.service.util.RSAUtil;
import com.clint.mybilibili.service.util.TokenUtil;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.PreferredConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserAuthService userAuthService;

    /**
     * 保存用户
     */
    public void saveUser(User user) {
        String phone = user.getPhone();
        // 判断手机号是否为空
        if (StringUtils.isNullOrEmpty(phone)) {
            throw new ConditionException("手机号不能为空！");
        }
        // 通过手机号查询用户
        User dbUser = getUserByPhone(phone);
        if (dbUser != null) {
            throw new ConditionException("该手机号已被注册！");
        }
        // 当前时间
        Date now = new Date();
        // 盐值
        String salt = String.valueOf(now.getTime());
        String password = user.getPassword();
        try {
            // 对传输密码进行解密
            password = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解密失败！");
        }
        // 进行 MD5 加密
        String md5Password = MD5Util.sign(password, salt, "UTF-8");
        // 补全注册用户
        user.setSalt(salt);
        user.setPassword(md5Password);
        user.setCreateTime(now);
        user.setUpdateTime(now);
        // 添加用户
        userDao.saveUser(user);
        // 添加用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setNick(UserConstant.DEFAULT_NICK);
        userInfo.setBirth(UserConstant.DEFAULT_BIRTH);
        userInfo.setGender(UserConstant.GENDER_MALE);
        userInfo.setCreateTime(now);
        userInfo.setUpdateTime(now);
        userDao.saveUserInfo(userInfo);
        // 为用户分配默认角色
        userAuthService.saveUserDefaultRole(user.getId());
    }

    /**
     * 根据手机号获取用户
     *
     * @param phone 手机号
     * @return 用户
     */
    public User getUserByPhone(String phone) {
        return userDao.getUserByPhone(phone);
    }

    /**
     * 用户登录
     *
     * @return 用户令牌
     */
    public String login(User user) throws Exception {
        String phone = user.getPhone();
        if (StringUtils.isNullOrEmpty(phone)) {
            throw new ConditionException("手机号不能为空！");
        }
        User dbUser = userDao.getUserByPhone(phone);
        if (dbUser == null) {
            throw new ConditionException("当前用户不存在！");
        }
        String password = user.getPassword();
        try {
            password = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解密失败！");
        }
        // 通过手机号查询到的用户获取盐值对密码进行加密
        String md5Password = MD5Util.sign(password, dbUser.getSalt(), "UTF-8");
        // 判断加密后的密码与查询用户的密码是否一致
        if (!md5Password.equalsIgnoreCase(dbUser.getPassword())) {
            throw new ConditionException("密码错误！");
        }
        return TokenUtil.generateToken(dbUser.getId());
    }

    /**
     * 根据用户 ID 获取用户信息
     *
     * @param userId 用户 ID
     */
    public User getUserInfo(Long userId) {
        User user = userDao.getUserById(userId);
        UserInfo userInfo = userDao.getUserInfoByUserId(userId);
        user.setUserInfo(userInfo);
        return user;
    }

    /**
     * 修改用户信息
     */
    public void updateUserInfos(UserInfo userInfo) {
        userInfo.setUpdateTime(new Date());
        userDao.updateUserInfos(userInfo);
    }

    /**
     * 根据 ID 获取用户
     */
    public User getUserById(Long id) {
        User user = userDao.getUserById(id);
        return user;
    }

    /**
     * 根据用户 ID 批量查询用户信息
     *
     * @param followingIdSet 关注用户 ID 集合
     * @return 用户信息集合
     */
    public List<UserInfo> getUserInfoByUserIds(Set<Long> followingIdSet) {
        List<UserInfo> userInfoList = userDao.getUserInfoByUserIds(followingIdSet);
        return userInfoList;
    }

    /**
     * 分页获取用户信息
     */
    public PageResult<UserInfo> pageListUserInfos(JSONObject params) {
        Integer no = params.getInteger("no");
        Integer size = params.getInteger("size");
        params.put("start", (no - 1) * size);
        params.put("limit", size);
        // 获取符合条件的总条数
        Integer count = userDao.pageCountUserInfos(params);
        List<UserInfo> userInfoList = new ArrayList<>();
        // 判断总条数是否大于 0
        if (count > 0) {
            userInfoList = userDao.pageListUserInfos(params);
        }
        return new PageResult<>(count, userInfoList);
    }

    /**
     * 用户登录
     *
     * @param user 用户信息实体
     * @return 封装双 token 的 Map
     */
    public Map<String, Object> loginForDts(User user) throws Exception {
        String phone = user.getPhone();
        if (StringUtils.isNullOrEmpty(phone)) {
            throw new ConditionException("手机号不能为空！");
        }
        User dbUser = userDao.getUserByPhone(phone);
        if (dbUser == null) {
            throw new ConditionException("当前用户不存在！");
        }
        String password = user.getPassword();
        try {
            password = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解密失败！");
        }
        // 通过手机号查询到的用户获取盐值对密码进行加密
        String md5Password = MD5Util.sign(password, dbUser.getSalt(), "UTF-8");
        // 判断加密后的密码与查询用户的密码是否一致
        if (!md5Password.equalsIgnoreCase(dbUser.getPassword())) {
            throw new ConditionException("密码错误！");
        }
        Long userId = dbUser.getId();
        String accessToken = TokenUtil.generateToken(userId);
        String refreshToken = TokenUtil.generateRefreshToken(userId);
        // 将 refresh token 保存到数据库
        userDao.removeRefreshToken(userId, refreshToken);
        userDao.saveRefreshToken(userId, refreshToken, new Date());
        Map<String, Object> result = new HashMap<>();
        result.put("accessToken", accessToken);
        result.put("refreshToken", refreshToken);
        return result;
    }

    /**
     * 退出登录
     * @param userId 用户 ID
     * @param refreshToken 刷新令牌
     */
    public void logout(Long userId, String refreshToken) {
        userDao.removeRefreshToken(userId, refreshToken);
    }

    /**
     * 刷新 token
     */
    public String refreshAccessToken(String refreshToken) throws Exception {
        RefreshTokenDetail refreshTokenDetail = userDao.getRefreshToken(refreshToken);
        if (refreshTokenDetail == null) {
            throw new ConditionException("555", "token已过期！");
        }
        Long userId = refreshTokenDetail.getUserId();
        return TokenUtil.generateToken(userId);
    }
}
