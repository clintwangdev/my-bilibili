package com.clint.mybilibili.api;

import com.clint.mybilibili.api.support.UserSupport;
import com.clint.mybilibili.domain.JsonResponse;
import com.clint.mybilibili.domain.UserMoments;
import com.clint.mybilibili.service.UserMomentsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(tags = "用户动态接口")
public class UserMomentsApi {

    @Autowired
    private UserMomentsService userMomentsService;

    @Autowired
    private UserSupport userSupport;

    @PostMapping("/user-moments")
    @ApiOperation(value = "添加用户动态")
    public JsonResponse<String> saveUserMoments(@RequestBody UserMoments userMoments) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        Long userId = userSupport.getCurrentId();
        userMoments.setUserId(userId);
        userMomentsService.saveUserMoments(userMoments);
        return JsonResponse.success();
    }

    @GetMapping("/user-subscribed-moments")
    @ApiOperation(value = "获取用户订阅up的动态")
    public JsonResponse<List<UserMoments>> getUserSubscribedMoments() {
        Long userId = userSupport.getCurrentId();
        List<UserMoments> userMomentsList = userMomentsService.getUserSubscribedMoments(userId);
        return JsonResponse.success(userMomentsList);
    }
}
