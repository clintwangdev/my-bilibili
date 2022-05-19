package com.clint.mybilibili.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "文件")
public class File {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "文件存储路径")
    private String url;

    @ApiModelProperty(value = "文件类型")
    private String type;

    @ApiModelProperty(value = "文件md5唯一标识串")
    private String md5;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
