package com.clint.mybilibili.service;

import com.clint.mybilibili.domain.auth.AuthRoleElementOperation;
import com.clint.mybilibili.domain.auth.AuthRoleMenu;
import com.clint.mybilibili.domain.auth.UserAuthorities;
import com.clint.mybilibili.domain.auth.UserRole;
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
}
