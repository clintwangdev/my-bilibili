package com.clint.mybilibili.api.support;

import com.clint.mybilibili.domain.exception.ConditionException;
import com.clint.mybilibili.service.util.TokenUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class UserSupport {

    /**
     * 获取当前用户 ID
     */
    public Long getCurrentId() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        // 通过请求头获取 token
        assert requestAttributes != null;
        String token = requestAttributes.getRequest().getHeader("token");
        // 验证 token 获取 userId
        Long userId = TokenUtil.verifyToken(token);
        if (userId < 0) {
            throw new ConditionException("非法用户！");
        }
        return userId;
    }
}
