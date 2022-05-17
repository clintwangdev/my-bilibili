package com.clint.mybilibili.service;

import com.clint.mybilibili.dao.AuthRoleDao;
import com.clint.mybilibili.domain.auth.AuthRole;
import com.clint.mybilibili.domain.auth.AuthRoleElementOperation;
import com.clint.mybilibili.domain.auth.AuthRoleMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class AuthRoleService {

    @Autowired
    private AuthRoleElementOperationService authRoleElementOperationService;

    @Autowired
    private AuthRoleMenuService authRoleMenuService;

    @Autowired
    private AuthRoleDao authRoleDao;

    /**
     * 根据角色 ID 集合查询角色元素权限
     */
    public List<AuthRoleElementOperation> getRoleElementOperationsByRoleIds(Set<Long> roleIdSet) {
        return authRoleElementOperationService.getRoleElementOperationsByRoleIds(roleIdSet);
    }

    /**
     * 根据角色 ID 集合查询角色菜单权限
     */
    public List<AuthRoleMenu> getRoleMenusByRoleIds(Set<Long> roleIdSet) {
        return authRoleMenuService.getRoleMenusByRoleIds(roleIdSet);
    }

    /**
     * 根据角色编码获取角色
     */
    public AuthRole getRoleByCode(String code) {
        return authRoleDao.getRoleByCode(code);
    }
}
