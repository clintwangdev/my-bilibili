package com.clint.mybilibili.api.aspect;

import com.clint.mybilibili.api.support.UserSupport;
import com.clint.mybilibili.domain.UserMoments;
import com.clint.mybilibili.domain.annotation.ApiLimitedRole;
import com.clint.mybilibili.domain.auth.UserRole;
import com.clint.mybilibili.domain.constant.AuthRoleConstant;
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
 * 参数权限控制
 */
@Order(1)
@Component
@Aspect
public class DataLimitedAspect {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserRoleService userRoleService;

    @Pointcut("@annotation(com.clint.mybilibili.domain.annotation.DataLimited)")
    public void check() {
    }

    @Before("check()")
    public void doBefore(JoinPoint joinPoint) {
        Long userId = userSupport.getCurrentId();
        // 获取用户对应角色
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);
        Set<String> roleCodeSet = userRoleList.stream()
                .map(UserRole::getRoleCode)
                .collect(Collectors.toSet());
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof UserMoments) {
                UserMoments userMoments = (UserMoments) arg;
                String type = userMoments.getType();
                // 用户等级不能为 Lv0，动态类型不能为  0：视频
                if (roleCodeSet.contains(AuthRoleConstant.ROLE_LV_0) && "0".equals(type)) {
                    throw new ConditionException("参数异常！");
                }
            }
        }
    }
}
