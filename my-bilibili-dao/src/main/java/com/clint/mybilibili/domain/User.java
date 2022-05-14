package com.clint.mybilibili.domain;

import lombok.Data;

import java.util.Date;

/**
 * 用户
 */
@Data
public class User {

    private Long id;

    private String phone;

    private String email;

    private String password;

    private String salt;

    private Date createTime;

    private Date updateTime;

    private UserInfo userInfo;
}
