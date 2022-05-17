package com.clint.mybilibili.service;

import com.clint.mybilibili.dao.UserRoleDao;
import com.clint.mybilibili.domain.auth.AuthRole;
import com.clint.mybilibili.domain.auth.UserRole;
import com.clint.mybilibili.domain.constant.AuthRoleConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserRoleService {

    @Autowired
    private UserRoleDao userRoleDao;

    @Autowired
    private AuthRoleService authRoleService;

    /**
     * 根据用户 ID 获取关联角色
     */
    public List<UserRole> getUserRoleByUserId(Long userId) {
        return userRoleDao.getUserRoleByUserId(userId);
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
        userRoleDao.saveUserRole(userRole);
    }

    /**
     * 添加用户角色
     */
    public void saveUserRole(UserRole userRole) {
        userRole.setCreateTime(new Date());
        userRoleDao.saveUserRole(userRole);
    }
}
