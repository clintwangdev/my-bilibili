package com.clint.mybilibili.service.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@ServerEndpoint("imserver")
public class WebSocketService {

    /**
     * 用于做日志记录
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final AtomicInteger ONLINE_COUNT = new AtomicInteger(0);

    private static final ConcurrentHashMap<String, WebSocketService> WEBSOCKET_MAP = new ConcurrentHashMap<>();

    private Session session;

    private String sessionId;

    /**
     * 通用 Spring 上下文对象，在启动项目时进行初始化
     */
    private static ApplicationContext APPLICATION_CONTEXT;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        WebSocketService.APPLICATION_CONTEXT = applicationContext;
    }

    @OnOpen
    public void openConnection(Session session) {
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

    }

    @OnError
    public void onError(Throwable error) {

    }

    private void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }
}
