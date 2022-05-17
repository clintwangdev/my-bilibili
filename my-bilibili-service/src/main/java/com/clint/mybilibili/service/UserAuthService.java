package com.clint.mybilibili.service;

import com.clint.mybilibili.domain.auth.*;
import com.clint.mybilibili.domain.constant.AuthRoleConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserAuthService {

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private AuthRoleService authRoleService;

    /**
     * 获取用户权限
     *
     * @return 封装所有权限的对象
     */
    public UserAuthorities getUserAuthorities(Long userId) {
        // 获取用户关联的角色
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);
        // 获取角色 ID 集合
        Set<Long> roleIdSet = userRoleList.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toSet());
        // 获取角色所拥有的页面元素权限
        List<AuthRoleElementOperation> authRoleElementOperationList = authRoleService
                .getRoleElementOperationsByRoleIds(roleIdSet);
        // 获取角色所拥有的菜单权限
        List<AuthRoleMenu> authRoleMenuList = authRoleService.getRoleMenusByRoleIds(roleIdSet);
        return new UserAuthorities(authRoleElementOperationList, authRoleMenuList);
    }

    /**
     * 为用户添加默认角色
     *
     * @param userId 用户 ID
     */
    public void saveUserDefaultRole(Long userId) {
        UserRole userRole = new UserRole();
        AuthRole role = authRoleService.getRoleByCode(AuthRoleConstant.ROLE_LV_0);
        userRole.setUserId(userId);
        userRole.setRoleId(role.getId());
        userRoleService.saveUserRole(userRole);
    }
}
