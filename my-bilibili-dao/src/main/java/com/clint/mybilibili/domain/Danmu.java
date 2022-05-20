package com.clint.mybilibili.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "弹幕")
public class Danmu {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("视频id")
    private Long videoId;

    @ApiModelProperty("弹幕内容")
    private String content;

    @ApiModelProperty("弹幕出现时间")
    private String danmuTime;

    @ApiModelProperty("创建时间")
    private Date createTime;
}
