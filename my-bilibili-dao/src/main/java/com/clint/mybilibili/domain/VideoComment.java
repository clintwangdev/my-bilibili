package com.clint.mybilibili.domain;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class VideoComment {

    private Long id;

    private Long videoId;

    private Long userId;

    private String comment;

    private Long replyUserId;

    private Long rootId;

    private Date createTime;

    private Date updateTime;

    /**
     * 子级评论列表
     */
    private List<VideoComment> childList;

    /**
     * 评论用户信息
     */
    private UserInfo userInfo;

    /**
     * 评论回复用户信息
     */
    private UserInfo replyUserInfo;
}
