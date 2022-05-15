package com.clint.mybilibili.service;

import com.clint.mybilibili.dao.UserDao;
import com.clint.mybilibili.domain.User;
import com.clint.mybilibili.domain.UserInfo;
import com.clint.mybilibili.domain.constant.UserConstant;
import com.clint.mybilibili.domain.exception.ConditionException;
import com.clint.mybilibili.service.util.MD5Util;
import com.clint.mybilibili.service.util.RSAUtil;
import com.clint.mybilibili.service.util.TokenUtil;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

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
}
