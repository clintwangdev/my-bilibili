package com.clint.mybilibili.api;

import com.alibaba.fastjson.JSONObject;
import com.clint.mybilibili.api.support.UserSupport;
import com.clint.mybilibili.domain.JsonResponse;
import com.clint.mybilibili.domain.PageResult;
import com.clint.mybilibili.domain.User;
import com.clint.mybilibili.domain.UserInfo;
import com.clint.mybilibili.service.UserFollowingService;
import com.clint.mybilibili.service.UserService;
import com.clint.mybilibili.service.util.RSAUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@Api(tags = "用户接口")
public class UserApi {

    @Autowired
    private UserService userService;

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserFollowingService userFollowingService;

    /**
     * 获取用户信息
     */
    @GetMapping("/users")
    @ApiOperation(value = "获取用户信息")
    public JsonResponse<User> getUserInfo() {
        Long userId = userSupport.getCurrentId();
        User user = userService.getUserInfo(userId);
        return JsonResponse.success(user);
    }

    /**
     * 获取 RSA 公钥
     */
    @GetMapping("/rsa-pks")
    @ApiOperation(value = "获取 RSA 公钥")
    public JsonResponse<String> getRsaPublicKey() {
        String publicKey = RSAUtil.getPublicKeyStr();
        return JsonResponse.success(publicKey);
    }

    /**
     * 用户注册
     */
    @PostMapping("/users")
    @ApiOperation(value = "用户注册")
    public JsonResponse<String> saveUser(@RequestBody User user) {
        userService.saveUser(user);
        return JsonResponse.success();
    }

    /**
     * 用户登录
     */
    @PostMapping("/user-tokens")
    @ApiOperation(value = "用户登录")
    public JsonResponse<String> login(@RequestBody User user) throws Exception {
        String token = userService.login(user);
        return JsonResponse.success(token);
    }

    /**
     * 修改用户信息
     */
    @PutMapping("/user-infos")
    @ApiOperation(value = "修改用户信息")
    public JsonResponse<String> updateUserInfos(@RequestBody UserInfo userInfo) {
        Long userId = userSupport.getCurrentId();
        userInfo.setUserId(userId);
        userService.updateUserInfos(userInfo);
        return JsonResponse.success();
    }

    @GetMapping("/user-infos")
    @ApiOperation(value = "分页获取用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "no", value = "当前页数", required = true),
            @ApiImplicitParam(name = "size", value = "每页显示条数", required = true),
            @ApiImplicitParam(name = "nick", value = "用户昵称"),
    })
    public JsonResponse<PageResult<UserInfo>> PageListUserInfos(@RequestParam Integer no, @RequestParam Integer size, String nick) {
        Long userId = userSupport.getCurrentId();
        JSONObject params = new JSONObject();
        params.put("no", no);
        params.put("size", size);
        params.put("nick", nick);
        params.put("userId", userId);
        PageResult<UserInfo> pageList = userService.pageListUserInfos(params);
        if (pageList.getTotal() > 0) {
            // 判断是否为关注用户
            List<UserInfo> checkedUserInfoList = userFollowingService.checkFollowingStatus(pageList.getList(), userId);
            pageList.setList(checkedUserInfoList);
        }
        return JsonResponse.success(pageList);
    }
}
