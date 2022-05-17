package com.clint.mybilibili.domain.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@ApiModel(value = "用户权限")
public class UserAuthorities {

    @ApiModelProperty(value = "角色元素关联集合")
    private List<AuthRoleElementOperation> roleElementOperationList;

    @ApiModelProperty(value = "角色菜单关联集合")
    private List<AuthRoleMenu> roleMenuList;
}
