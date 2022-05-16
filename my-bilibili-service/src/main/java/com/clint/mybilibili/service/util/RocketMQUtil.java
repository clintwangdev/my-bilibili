package com.clint.mybilibili.service.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.CountDownLatch2;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.concurrent.TimeUnit;

/**
 * RocketMQ 工具类
 */
@Slf4j
public class RocketMQUtil {

    /**
     * 同步发送消息
     *
     * @param producer 消息生产者
     * @param msg      消息内容
     */
    public static void syncSentMsg(DefaultMQProducer producer, Message msg) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        SendResult result = producer.send(msg);
    }

    /**
     * 异步发送消息
     *
     * @param producer 消息生产者
     * @param msg      消息内容
     */
    public static void asyncSendMsg(DefaultMQProducer producer, Message msg) throws RemotingException, InterruptedException, MQClientException {
        int messageCount = 2;
        CountDownLatch2 countDownLatch = new CountDownLatch2(messageCount);
        for (int i = 0; i < messageCount; ++i) {
            producer.send(msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    countDownLatch.countDown();
                    log.info(sendResult.toString());
                }

                @Override
                public void onException(Throwable throwable) {
                    countDownLatch.countDown();
                    log.error("发送消息产生了异常！" + throwable.getMessage());
                }
            });
        }
        countDownLatch.await(5, TimeUnit.SECONDS);
    }
}
