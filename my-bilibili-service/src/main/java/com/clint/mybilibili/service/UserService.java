package com.clint.mybilibili.service;

import com.clint.mybilibili.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public Long query(Long id) {
        return userDao.query(id);
    }
}
