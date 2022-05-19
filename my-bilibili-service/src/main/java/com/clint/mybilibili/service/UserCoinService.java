package com.clint.mybilibili.service;

import com.clint.mybilibili.dao.UserCoinDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserCoinService {

    @Autowired
    private UserCoinDao userCoinDao;

    /**
     * 获取用户硬币数
     */
    public Long getUserCoinAmount(Long userId) {
        return userCoinDao.getUserCoinAmount(userId);
    }

    /**
     * 更新当前用户硬币总数
     */
    public void updateUserCoinAmount(Long userId, Long amount) {
        userCoinDao.updateUserCoinAmount(userId, amount, new Date());
    }
}
