package com.clint.mybilibili.service.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.clint.mybilibili.domain.exception.ConditionException;

import java.util.Calendar;
import java.util.Date;

/**
 * 令牌工具类
 */
public class TokenUtil {

    private static final String ISSUER = "clint";

    /**
     * 生成 token
     *
     * @param userId 用户 ID
     * @return 令牌
     */
    public static String generateToken(Long userId) throws Exception {
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, 30);
        return JWT.create().withKeyId(String.valueOf(userId))
                .withIssuer(ISSUER)
                .withExpiresAt(calendar.getTime())
                .sign(algorithm);
    }

    /**
     * 验证 token
     *
     * @param token 令牌
     * @return 用户 ID
     */
    public static Long verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            String userId = jwt.getKeyId();
            return Long.parseLong(userId);
        } catch (TokenExpiredException e) {
            throw new ConditionException("555", "token已过期！");
        } catch (Exception e) {
            throw new ConditionException("非法用户token！");
        }
    }
}
