package com.clint.mybilibili.domain;

import lombok.Data;

import java.util.Date;

@Data
public class VideoView {

    private Long id;

    private Long videoId;

    private Long userId;

    private String clientId;

    private String ip;

    private Date createTime;
}
