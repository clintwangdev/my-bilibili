package com.clint.mybilibili.service.websocket;

import com.alibaba.fastjson.JSONObject;
import com.clint.mybilibili.domain.Danmu;
import com.clint.mybilibili.domain.constant.MQConstant;
import com.clint.mybilibili.service.util.RocketMQUtil;
import com.clint.mybilibili.service.util.TokenUtil;
import io.netty.util.internal.StringUtil;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@ServerEndpoint("/imserver/{token}")
public class WebSocketService {

    /**
     * 用于做日志记录
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final AtomicInteger ONLINE_COUNT = new AtomicInteger(0);

    public static final ConcurrentHashMap<String, WebSocketService> WEBSOCKET_MAP = new ConcurrentHashMap<>();

    private Session session;

    private String sessionId;

    private Long userId;

    /**
     * 通用 Spring 上下文对象，在启动项目时进行初始化
     */
    private static ApplicationContext APPLICATION_CONTEXT;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        WebSocketService.APPLICATION_CONTEXT = applicationContext;
    }

    @OnOpen
    public void openConnection(Session session, @PathParam("token") String token) {
        try {
            // 解析token获取用户id
            this.userId = TokenUtil.verifyToken(token);
        } catch (Exception ignored) {
        }
        this.sessionId = session.getId();
        this.session = session;
        if (WEBSOCKET_MAP.containsKey(sessionId)) {
            WEBSOCKET_MAP.remove(sessionId);
            WEBSOCKET_MAP.put(sessionId, this);
        } else {
            WEBSOCKET_MAP.put(sessionId, this);
            // 在线人数+1
            ONLINE_COUNT.getAndIncrement();
        }
        logger.info("用户连接成功：{}，当前在线人数为：{}", sessionId, ONLINE_COUNT.get());
        try {
            this.sendMessage("0");
        } catch (Exception e) {
            logger.error("连接异常");
        }
    }

    @OnClose
    public void closeConnection() {
        if (WEBSOCKET_MAP.containsKey(sessionId)) {
            WEBSOCKET_MAP.remove(sessionId);
            // 在线人数-1
            ONLINE_COUNT.getAndDecrement();
        }
        logger.info("用户退出：{}，当前在线人数为：{}", sessionId, ONLINE_COUNT.get());
    }

    @OnMessage
    public void onMessage(String message) {
        logger.info("用户信息：{}，内容：{}", sessionId, message);
        if (!StringUtil.isNullOrEmpty(message)) { // 如果消息不为空
            try {
                // 遍历每一个处理打开状态的WebSocketService
                for (Map.Entry<String, WebSocketService> entry : WEBSOCKET_MAP.entrySet()) {
                    DefaultMQProducer danmusProducer = APPLICATION_CONTEXT.getBean("danmusProducer", DefaultMQProducer.class);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("sessionId", session);
                    jsonObject.put("message", message);
                    Message msg = new Message(MQConstant.TOPIC_DANMUS, JSONObject.toJSONString(jsonObject).getBytes(StandardCharsets.UTF_8));
                    RocketMQUtil.asyncSendMsg(danmusProducer, msg);
                }
                if (userId != null) { // 如果不是游客
                    Danmu danmu = JSONObject.parseObject(message, Danmu.class);
                    danmu.setCreateTime(new Date());
                    danmu.setUserId(userId);
                    DanmuService danmuService = APPLICATION_CONTEXT.getBean("danmuService", DanmuService.class);
                    // 异步保存弹幕到数据库
                    danmuService.asyncSaveDanmu(danmu);
                    // 保存弹幕到Redis
                    danmuService.saveDanmuToRedis(danmu);
                }
            } catch (Exception e) {
                logger.error("弹幕接收失败！");
                e.printStackTrace();
            }
        }
    }

    @OnError
    public void onError(Throwable error) {

    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    public Session getSession() {
        return session;
    }

    public String getSessionId() {
        return sessionId;
    }

    @Scheduled(fixedRate = 5000)
    public void noticeOnlineCount() throws IOException {
        for (Map.Entry<String, WebSocketService> entry : WEBSOCKET_MAP.entrySet()) {
            WebSocketService webSocketService = entry.getValue();
            if (webSocketService.getSession().isOpen()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("onlineCount", ONLINE_COUNT.get());
                jsonObject.put("msg", "当前在线人数为：" + ONLINE_COUNT.get());
                this.sendMessage(jsonObject.toJSONString());
            }
        }
    }
}
