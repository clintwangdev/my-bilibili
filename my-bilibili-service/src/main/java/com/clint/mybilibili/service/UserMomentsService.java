package com.clint.mybilibili.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.clint.mybilibili.dao.UserMomentsDao;
import com.clint.mybilibili.domain.UserMoments;
import com.clint.mybilibili.domain.constant.UserMomentsConstant;
import com.clint.mybilibili.service.util.RocketMQUtil;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Service
public class UserMomentsService {

    @Autowired
    private UserMomentsDao userMomentsDao;

    @Autowired
    private DefaultMQProducer momentsProducer;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 添加用户动态
     */
    public void saveUserMoments(UserMoments userMoments) throws MQBrokerException, RemotingException,
            InterruptedException, MQClientException {
        Date now = new Date();
        userMoments.setCreateTime(now);
        userMoments.setUpdateTime(now);
        userMomentsDao.saveUserMoments(userMoments);
        // 向 RocketMQ 发送消息
        Message msg = new Message(UserMomentsConstant.TOPIC_MOMENTS, JSONObject.toJSONString(userMoments)
                .getBytes(StandardCharsets.UTF_8));
        RocketMQUtil.syncSentMsg(momentsProducer, msg);
    }

    /**
     * 获取用户订阅up的动态
     *
     * @param userId 当前用户 ID
     * @return 订阅up的动态
     */
    public List<UserMoments> getUserSubscribedMoments(Long userId) {
        String key = "subscribed-" + userId;
        String listStr = redisTemplate.opsForValue().get(key);
        return JSONArray.parseArray(listStr, UserMoments.class);
    }
}
