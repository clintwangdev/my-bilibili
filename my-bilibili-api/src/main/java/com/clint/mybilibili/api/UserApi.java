package com.clint.mybilibili.api;

import com.clint.mybilibili.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserApi {

    @Autowired
    private UserService userService;

    @GetMapping("/query")
    public Long query(Long id) {
        log.info(id.toString());
        return userService.query(id);
    }
}
