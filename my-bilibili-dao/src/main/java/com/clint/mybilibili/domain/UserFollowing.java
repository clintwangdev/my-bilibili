package com.clint.mybilibili.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 用户关注实体
 */
@Data
@ApiModel(value = "用户关注")
public class UserFollowing {

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 用户 ID
     */
    @ApiModelProperty(value = "用户 ID")
    private Long userId;

    /**
     * 关注用户 ID
     */
    @ApiModelProperty(value = "关注用户 ID")
    private Long followingId;

    /**
     * 关注分组 ID
     */
    @ApiModelProperty(value = "关注分组 ID")
    private Long groupId;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 用户信息
     */
    @ApiModelProperty(value = "用户信息")
    private UserInfo userInfo;
}
