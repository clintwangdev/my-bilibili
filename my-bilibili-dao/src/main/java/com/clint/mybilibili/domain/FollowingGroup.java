package com.clint.mybilibili.domain;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 用户关注分组实体
 */
@Data
public class FollowingGroup {

    /**
     * 主键
     */
    private Long id;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 关注分组名称
     */
    private String name;

    /**
     * 关注分组类型
     */
    private String type;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 关注用户信息集合
     */
    private List<UserInfo> followingUserInfoList;
}
