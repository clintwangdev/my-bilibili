package com.clint.mybilibili;

import com.clint.mybilibili.service.websocket.WebSocketService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableAsync
@EnableScheduling
public class MyBilibiliApplication {

    public static void main(String[] args) {
        ApplicationContext app = SpringApplication.run(MyBilibiliApplication.class, args);
        WebSocketService.setApplicationContext(app);
    }
}
