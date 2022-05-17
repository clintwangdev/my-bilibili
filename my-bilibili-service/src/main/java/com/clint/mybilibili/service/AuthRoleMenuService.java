package com.clint.mybilibili.service;

import com.clint.mybilibili.dao.AuthRoleMenuDao;
import com.clint.mybilibili.domain.auth.AuthRoleMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class AuthRoleMenuService {

    @Autowired
    private AuthRoleMenuDao authRoleMenuDao;

    /**
     * 根据角色 ID 获取角色菜单权限
     */
    public List<AuthRoleMenu> getRoleMenusByRoleIds(Set<Long> roleIdSet) {
        return authRoleMenuDao.getRoleMenusByRoleIds(roleIdSet);
    }
}
