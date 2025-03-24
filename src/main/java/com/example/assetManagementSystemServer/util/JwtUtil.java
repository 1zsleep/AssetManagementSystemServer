package com.example.assetManagementSystemServer.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * JWT 工具类
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey; // JWT密钥

    @Value("${jwt.expiration}")
    private Long expiration;// JWT过期时间

    /**
     * 获取用于签名的算法
     * @return Algorithm
     */
    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(secretKey);
    }

    /**
     * 从 JWT 中提取用户名
     * @param token JWT
     * @return 用户名
     */
    public String extractUsername(String token) {
        return getDecodedJWT(token).getSubject();
    }

    /**
     * 从 JWT 中提取过期时间
     * @param token JWT
     * @return 过期时间
     */
    public Date extractExpiration(String token) {
        return getDecodedJWT(token).getExpiresAt();
    }

    /**
     * 解析 JWT
     * @param token JWT
     * @return DecodedJWT
     */
    private DecodedJWT getDecodedJWT(String token) {
        JWTVerifier verifier = JWT.require(getAlgorithm()).build();
        return verifier.verify(token);
    }

    /**
     * 检查 JWT 是否已过期
     * @param token JWT
     * @return 是否过期
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * 生成 JWT
     * @param username 用户名
     * @return JWT
     */
    public String generateToken(String username,boolean status, Collection<? extends GrantedAuthority> authorities) {
        return JWT.create()
                .withSubject(username)
                .withClaim("status", status)
                .withClaim("roles", authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())) // 添加角色声明
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
                .sign(getAlgorithm());
    }


    /**
     * 验证 JWT
     * @param token JWT
     * @param username 用户名
     * @return 是否有效
     */
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}