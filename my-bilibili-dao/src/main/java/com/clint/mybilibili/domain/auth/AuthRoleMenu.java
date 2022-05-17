package com.clint.mybilibili.domain.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "角色与页面菜单关联")
public class AuthRoleMenu {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "角色 ID")
    private Long roleId;

    @ApiModelProperty(value = "页面菜单 ID")
    private Long elementOperationId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
