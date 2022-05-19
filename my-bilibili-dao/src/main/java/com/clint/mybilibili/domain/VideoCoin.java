package com.clint.mybilibili.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;


@Data
@ApiModel(value = "视频硬币")
public class VideoCoin {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "视频投稿id")
    private Long videoId;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "投币数")
    private Long amount;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}
