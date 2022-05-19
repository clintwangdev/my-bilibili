package com.clint.mybilibili.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "视频收藏")
public class VideoCollection {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "视频id")
    private Long videoId;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "收藏分组id")
    private Long groupId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
