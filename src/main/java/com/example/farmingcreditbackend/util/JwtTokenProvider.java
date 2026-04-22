package com.example.farmingcreditbackend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * JWT Token工具类
 */
@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.base64-secret}")
    private String base64Secret;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private String secretKey;

    @PostConstruct
    public void init() {
        // 优先使用base64Secret，如果没有则使用普通secret
        if (base64Secret != null && !base64Secret.isEmpty()) {
            secretKey = Base64.getEncoder().encodeToString(base64Secret.getBytes());
        } else {
            secretKey = Base64.getEncoder().encodeToString(secret.getBytes());
        }
    }

    /**
     * 生成Token
     */
    public String generateToken(Long userId, String username, List<String> roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration * 1000);

        String token = Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("roles", roles)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();

        // 存储到Redis，设置过期时间
        String redisKey = "TOKEN:" + username;
        redisTemplate.opsForValue().set(
                redisKey,
                token,
                expiration,
                TimeUnit.SECONDS
        );

        return token;
    }

    /**
     * 从Token获取用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    /**
     * 从Token获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 从Token获取角色列表
     */
    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("roles", List.class);
    }

    /**
     * 验证Token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);

            // 检查Redis中是否存在
            String username = getUsernameFromToken(token);
            String redisKey = "TOKEN:" + username;
            String redisToken = (String) redisTemplate.opsForValue().get(redisKey);

            if (redisToken == null) {
                return false;
            }

            return token.equals(redisToken);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("无效的JWT token", e);
            return false;
        }
    }

    /**
     * 使Token失效
     */
    public void invalidateToken(String token) {
        try {
            String username = getUsernameFromToken(token);
            String redisKey = "TOKEN:" + username;
            redisTemplate.delete(redisKey);
        } catch (Exception e) {
            log.error("使Token失效失败", e);
        }
    }

    /**
     * 获取Token剩余过期时间（秒）
     */
    public Long getExpiration(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            long now = System.currentTimeMillis();
            long exp = expiration.getTime();
            return (exp - now) / 1000;
        } catch (Exception e) {
            return 0L;
        }
    }

    /**
     * 刷新Token
     */
    public String refreshToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            String username = claims.getSubject();
            Long userId = claims.get("userId", Long.class);
            @SuppressWarnings("unchecked")
            List<String> roles = claims.get("roles", List.class);

            // 使旧Token失效
            invalidateToken(token);

            // 生成新Token
            return generateToken(userId, username, roles);
        } catch (Exception e) {
            log.error("刷新Token失败", e);
            return null;
        }
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }
}