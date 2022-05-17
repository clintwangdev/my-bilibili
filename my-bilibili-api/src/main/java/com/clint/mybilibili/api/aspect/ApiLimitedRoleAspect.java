package com.clint.mybilibili.api.aspect;

import com.clint.mybilibili.api.support.UserSupport;
import com.clint.mybilibili.domain.annotation.ApiLimitedRole;
import com.clint.mybilibili.domain.auth.UserRole;
import com.clint.mybilibili.domain.exception.ConditionException;
import com.clint.mybilibili.service.UserRoleService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 接口权限控制
 */
@Order(1)
@Component
@Aspect
public class ApiLimitedRoleAspect {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserRoleService userRoleService;

    @Pointcut("@annotation(com.clint.mybilibili.domain.annotation.ApiLimitedRole)")
    public void check() {
    }

    @Before("check() && @annotation(apiLimitedRole)")
    public void doBefore(JoinPoint joinPoint, ApiLimitedRole apiLimitedRole) {
        Long userId = userSupport.getCurrentId();
        // 获取用户对应角色
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);
        String[] limitedRoleCodeList = apiLimitedRole.limitedRoleCodeList();
        Set<String> limitedRoleCodeSet = Arrays.stream(limitedRoleCodeList)
                .collect(Collectors.toSet());
        Set<String> roleCodeSet = userRoleList.stream()
                .map(UserRole::getRoleCode)
                .collect(Collectors.toSet());
        // 取交集
        roleCodeSet.retainAll(limitedRoleCodeSet);
        if (roleCodeSet.size() > 0) {
            throw new ConditionException("权限不足！");
        }
    }
}
