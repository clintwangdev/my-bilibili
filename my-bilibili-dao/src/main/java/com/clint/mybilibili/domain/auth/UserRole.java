package com.clint.mybilibili.domain.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "用户角色关联")
public class UserRole {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "用户 ID")
    private Long userId;

    @ApiModelProperty(value = "角色 ID")
    private Long roleId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
