package com.clint.mybilibili.service;

import com.clint.mybilibili.dao.FollowingGroupDao;
import com.clint.mybilibili.domain.FollowingGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FollowingGroupService {

    @Autowired
    private FollowingGroupDao followingGroupDao;

    /**
     * 根据分组类型获取分组
     */
    public FollowingGroup getByType(String type) {
        return followingGroupDao.getByType(type);
    }

    /**
     * 根据 ID 获取分组
     */
    public FollowingGroup getById(Long id) {
        return followingGroupDao.getById(id);
    }

    public List<FollowingGroup> getByUserId(Long userId) {
        return followingGroupDao.getByUserId(userId);
    }
}
