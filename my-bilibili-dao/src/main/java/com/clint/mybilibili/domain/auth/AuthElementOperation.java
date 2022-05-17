package com.clint.mybilibili.domain.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "页面元素操作")
public class AuthElementOperation {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "页面元素名称")
    private String elementName;

    @ApiModelProperty(value = "页面元素唯一编码")
    private String elementCode;

    @ApiModelProperty(value = "操作类型: 0可点击, 1可见")
    private String operationType;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}
