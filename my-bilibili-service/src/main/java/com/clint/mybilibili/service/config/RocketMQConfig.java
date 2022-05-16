package com.clint.mybilibili.service.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.clint.mybilibili.domain.UserFollowing;
import com.clint.mybilibili.domain.UserMoments;
import com.clint.mybilibili.domain.constant.UserMomentsConstant;
import com.clint.mybilibili.service.UserFollowingService;
import com.mysql.cj.util.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * RocketMQ 配置类
 */
@Configuration
public class RocketMQConfig {

    @Value("${rocketmq.name.server.address}")
    private String nameServerAddr;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserFollowingService userFollowingService;

    /**
     * 消息生产者
     */
    @Bean("momentsProducer")
    public DefaultMQProducer momentsProducer() throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer(UserMomentsConstant.GROUP_MOMENTS);
        producer.setNamesrvAddr(nameServerAddr);
        producer.start();
        return producer;
    }

    /**
     * 消息消费者
     */
    @Bean("momentsConsumer")
    public DefaultMQPushConsumer momentsConsumer() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(UserMomentsConstant.GROUP_MOMENTS);
        consumer.setNamesrvAddr(nameServerAddr);
        consumer.subscribe(UserMomentsConstant.TOPIC_MOMENTS, "*");
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, consumeConcurrentlyContext) -> {
            MessageExt msg = msgs.get(0);
            if (msg == null) {
                // 如果消息为空，直接返回
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
            String bodyStr = new String(msg.getBody());
            UserMoments userMoments = JSONObject.toJavaObject(JSONObject.parseObject(bodyStr), UserMoments.class);
            Long userId = userMoments.getUserId();
            // 获取关注了当前用户的粉丝列表
            List<UserFollowing> fans = userFollowingService.getUserFans(userId);
            for (UserFollowing fan : fans) {
                String key = "subscribed-" + fan.getUserId();
                String subscribedListStr = redisTemplate.opsForValue().get(key);
                List<UserMoments> subscribedList;
                if (StringUtils.isNullOrEmpty(subscribedListStr)) {
                    // 如果订阅列表为空，创建一个新列表
                    subscribedList = new ArrayList<>();
                } else {
                    // 如果不为空，将订阅列表字符串转换为列表
                    subscribedList = JSONArray.parseArray(subscribedListStr, UserMoments.class);
                }
                subscribedList.add(userMoments);
                // 将列表转为字符串存入缓存
                redisTemplate.opsForValue().set(key, JSONObject.toJSONString(subscribedList));
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
        return consumer;
    }
}
