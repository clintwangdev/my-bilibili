package com.clint.mybilibili.domain;

import lombok.Data;

import java.util.Date;

/**
 * 用户信息
 */
@Data
public class UserInfo {

    private Long id;

    private Long userId;

    private String nick;

    private String avatar;

    private String sign;

    private String gender;

    private String birth;

    private Date createTime;

    private Date updateTime;

    private Boolean followed;
}
