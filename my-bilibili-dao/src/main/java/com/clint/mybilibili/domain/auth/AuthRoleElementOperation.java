package com.clint.mybilibili.domain.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "角色与元素关联")
public class AuthRoleElementOperation {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "角色 ID")
    private Long roleId;

    @ApiModelProperty(value = "元素操作 ID")
    private Long elementOperationId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    private AuthElementOperation authElementOperation;
}
