package com.clint.mybilibili.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 用户关注分组实体
 */
@Data
@ApiModel(value = "用户关注")
public class FollowingGroup {

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
     * 关注分组名称
     */
    @ApiModelProperty(value = "关注分组名称")
    private String name;

    /**
     * 关注分组类型
     */
    @ApiModelProperty(value = "关注分组类型")
    private String type;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    /**
     * 关注用户信息集合
     */
    @ApiModelProperty(value = "关注用户信息集合")
    private List<UserInfo> followingUserInfoList;
}
