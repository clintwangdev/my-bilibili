package com.clint.mybilibili.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "视频标签")
public class VideoTag {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "视频id")
    private Long videoId;

    @ApiModelProperty(value = "标签id")
    private Long tagId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
