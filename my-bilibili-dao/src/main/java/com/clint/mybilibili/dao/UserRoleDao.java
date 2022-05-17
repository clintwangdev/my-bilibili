package com.clint.mybilibili.dao;

import com.clint.mybilibili.domain.auth.UserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserRoleDao {

    List<UserRole> getUserRoleByUserId(Long userId);

    void saveUserRole(UserRole userRole);
}
