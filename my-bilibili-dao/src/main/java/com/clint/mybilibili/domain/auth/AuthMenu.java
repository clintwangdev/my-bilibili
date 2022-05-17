package com.clint.mybilibili.domain.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "页面访问菜单")
public class AuthMenu {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "菜单项目名称")
    private String name;

    @ApiModelProperty(value = "唯一编码")
    private String code;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}
