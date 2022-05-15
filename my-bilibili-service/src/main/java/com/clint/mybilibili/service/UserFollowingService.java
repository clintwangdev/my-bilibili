package com.clint.mybilibili.service;

import com.clint.mybilibili.dao.UserFollowingDao;
import com.clint.mybilibili.domain.FollowingGroup;
import com.clint.mybilibili.domain.User;
import com.clint.mybilibili.domain.UserFollowing;
import com.clint.mybilibili.domain.UserInfo;
import com.clint.mybilibili.domain.constant.UserConstant;
import com.clint.mybilibili.domain.exception.ConditionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserFollowingService {

    @Autowired
    private UserFollowingDao userFollowingDao;

    @Autowired
    private FollowingGroupService followingGroupService;

    @Autowired
    private UserService userService;

    /**
     * 添加用户关注
     */
    @Transactional
    public void saveUserFollowings(UserFollowing userFollowing) {
        Long groupId = userFollowing.getGroupId();
        // 判断用户是否指定分组
        if (groupId == null) {
            // 如果没有指定分组
            FollowingGroup followingGroup = followingGroupService.getByType(UserConstant.DEFAULT_USER_FOLLOWING_GROUP_TYPE);
            // 设置默认分组 ID
            userFollowing.setGroupId(followingGroup.getId());
        } else {
            // 用户指定了分组
            FollowingGroup followingGroup = followingGroupService.getById(userFollowing.getGroupId());
            if (followingGroup == null) {
                // 分组信息不存在
                throw new ConditionException("分组不存在！");
            }
        }
        // 判断关注用户是否存在
        Long followingId = userFollowing.getFollowingId();
        User user = userService.getUserById(followingId);
        if (user == null) {
            throw new ConditionException("关注用户不存在！");
        }
        // 删除用户与关注用户的关联
        userFollowingDao.removeUserFollowing(userFollowing.getUserId(), followingId);
        userFollowing.setCreateTime(new Date());
        // 添加用户关注
        userFollowingDao.saveUserFollowing(userFollowing);
    }

    /**
     * 获取关注列表及其用户信息
     */
    public List<FollowingGroup> getUserFollowings(Long userId) {
        // 获取用户关注用户列表
        List<UserFollowing> userFollowings = userFollowingDao.getUserFollowings(userId);
        // 遍历得到关注用户 ID 集合
        Set<Long> followingIdSet = userFollowings.stream()
                .map(UserFollowing::getFollowingId)
                .collect(Collectors.toSet());
        List<UserInfo> userInfoList = new ArrayList<>();
        if (!followingIdSet.isEmpty()) {
            // 如果关注用户列表不为空, 批量查询用户信息
            userInfoList = userService.getUserInfoByUserIds(followingIdSet);
        }
        for (UserFollowing userFollowing : userFollowings) {
            for (UserInfo userInfo : userInfoList) {
                // 判断 '关注用户 ID' 和 '用户信息 ID' 是否相等
                boolean isEquals = userInfo.getUserId().equals(userFollowing.getFollowingId());
                if (isEquals) {
                    // 将用户信息传入用户关注对象
                    userFollowing.setUserInfo(userInfo);
                }
            }
        }
        // 将关注用户按关注分组进行分类
        // 获取登录用户所有分组列表
        List<FollowingGroup> followingGroupList = followingGroupService.getByUserId(userId);
        // 设置全部分组
        FollowingGroup allGroup = new FollowingGroup();
        allGroup.setName(UserConstant.USER_FOLLOWING_GROUP_ALL_NAME);
        // 将所有关注用户信息放入全部分组
        allGroup.setFollowingUserInfoList(userInfoList);
        // 定义关注分组集合存放返回结果
        List<FollowingGroup> result = new ArrayList<>();
        result.add(allGroup);
        // 遍历用户的关注分组
        for (FollowingGroup followingGroup : followingGroupList) {
            List<UserInfo> infoList = new ArrayList<>();
            for (UserFollowing userFollowing : userFollowings) {
                if (userFollowing.getGroupId().equals(followingGroup.getId())) {
                    // 如果分组 ID 相同, 追加用户信息
                    infoList.add(userFollowing.getUserInfo());
                }
            }
            // 将用户信息存入特定分组
            followingGroup.setFollowingUserInfoList(infoList);
            result.add(followingGroup);
        }
        return result;
    }

    /**
     * 获取用户粉丝列表及其信息
     */
    public List<UserFollowing> getUserFans(Long userId) {
        // 获取关注了当前用户的粉丝
        List<UserFollowing> fanList = userFollowingDao.getUserFans(userId);
        // 获取当前用户的所有粉丝 ID
        Set<Long> fanIdSet = fanList.stream()
                .map(UserFollowing::getUserId)
                .collect(Collectors.toSet());
        List<UserInfo> userInfoList = new ArrayList<>();
        // 根据粉丝 ID 集合批量查询粉丝的用户信息
        if (!fanIdSet.isEmpty()) {
            userInfoList = userService.getUserInfoByUserIds(fanIdSet);
        }
        List<UserFollowing> followingList = userFollowingDao.getUserFollowings(userId);
        for (UserFollowing fan : fanList) {
            // 将用户信息赋值给每一个粉丝
            for (UserInfo userInfo : userInfoList) {
                // 判断是否与用户信息的用户 ID 相等
                if (userInfo.getUserId().equals(fan.getUserId())) {
                    userInfo.setFollowed(false);
                    fan.setUserInfo(userInfo);
                }
            }
            // 判断是否为双向关注
            for (UserFollowing following : followingList) {
                if (following.getFollowingId().equals(fan.getUserId())) {
                    fan.getUserInfo().setFollowed(true);
                }
            }
        }
        return fanList;
    }

    /**
     * 添加关注分组
     *
     * @return 分组 ID
     */
    public Long saveUserFollowingGroup(FollowingGroup followingGroup) {
        Date now = new Date();
        followingGroup.setCreateTime(now);
        followingGroup.setUpdateTime(now);
        followingGroup.setType(UserConstant.USER_CUSTOM_FOLLOWING_GROUP_TYPE);
        return followingGroupService.saveFollowingGroup(followingGroup);
    }

    /**
     * 获取用户关注分组
     *
     * @param userId 用户 ID
     * @return 关注分组集合
     */
    public List<FollowingGroup> getUserFollowingGroups(Long userId) {
        return followingGroupService.getUserFollowingGroups(userId);
    }

    /**
     * 检查关注状态
     */
    public List<UserInfo> checkFollowingStatus(List<UserInfo> list, Long userId) {
        // 获取当前用户关注列表
        List<UserFollowing> userFollowingList = userFollowingDao.getUserFollowings(userId);
        // 将所有用户信息列表设置是否关注
        for (UserInfo userInfo : list) {
            userInfo.setFollowed(false);
            for (UserFollowing userFollowing : userFollowingList) {
                if (userFollowing.getFollowingId().equals(userInfo.getUserId())) {
                    userInfo.setFollowed(true);
                }
            }
        }
        return list;
    }
}
