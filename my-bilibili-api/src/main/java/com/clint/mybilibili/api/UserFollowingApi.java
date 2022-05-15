package com.clint.mybilibili.api;

import com.clint.mybilibili.api.support.UserSupport;
import com.clint.mybilibili.domain.FollowingGroup;
import com.clint.mybilibili.domain.JsonResponse;
import com.clint.mybilibili.domain.UserFollowing;
import com.clint.mybilibili.service.UserFollowingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(tags = "用户关注接口")
public class UserFollowingApi {

    @Autowired
    private UserFollowingService userFollowingService;

    @Autowired
    private UserSupport userSupport;

    /**
     * 添加关注
     */
    @PostMapping("/user-followings")
    @ApiOperation(value = "添加关注")
    public JsonResponse<String> saveUserFollowings(@RequestBody UserFollowing userFollowing) {
        Long userId = userSupport.getCurrentId();
        userFollowing.setUserId(userId);
        userFollowingService.saveUserFollowings(userFollowing);
        return JsonResponse.success("关注成功！");
    }

    /**
     * 获取用户关注列表
     */
    @GetMapping("/user-followings")
    @ApiOperation(value = "获取用户关注列表")
    public JsonResponse<List<FollowingGroup>> getUserFollowings() {
        Long userId = userSupport.getCurrentId();
        List<FollowingGroup> userFollowingList = userFollowingService.getUserFollowings(userId);
        return JsonResponse.success(userFollowingList);
    }

    /**
     * 获取当前用户粉丝
     */
    @GetMapping("/user-fans")
    @ApiOperation(value = "获取当前用户粉丝")
    public JsonResponse<List<UserFollowing>> getUserFans() {
        Long userId = userSupport.getCurrentId();
        List<UserFollowing> fanList = userFollowingService.getUserFans(userId);
        return JsonResponse.success(fanList);
    }

    /**
     * 新建用户关注分组
     */
    @PostMapping("/user-following-groups")
    @ApiOperation(value = "新建用户关注分组")
    public JsonResponse<Long> saveUserFollowingGroups(@RequestBody FollowingGroup followingGroup) {
        Long userId = userSupport.getCurrentId();
        followingGroup.setUserId(userId);
        Long groupId = userFollowingService.saveUserFollowingGroup(followingGroup);
        return JsonResponse.success(groupId);
    }

    /**
     * 获取用户关注分组
     */
    @GetMapping("/user-following-groups")
    @ApiOperation(value = "获取用户关注分组")
    public JsonResponse<List<FollowingGroup>> getUserFollowingGroups() {
        Long userId = userSupport.getCurrentId();
        List<FollowingGroup> followingGroupList = userFollowingService.getUserFollowingGroups(userId);
        return JsonResponse.success(followingGroupList);
    }
}
