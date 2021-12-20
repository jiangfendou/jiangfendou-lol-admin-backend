package com.jiangfendou.loladmin.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jiangfendou.jwt")
public class JwtUtils {

    private long expire;

    private String secret;

    private String header;

    /**
     * 生成jwt
     */
    public String generateToken(String username) {
        Date date = new Date();
        Date expireDate = new Date(date.getTime() + 1000 * expire);

       return Jwts.builder()
            .setHeaderParam("type", "JET")
            .setSubject(username)
            .setIssuedAt(date)
            // 7天过期
            .setExpiration(expireDate)
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact();
    }
    /**
     * 解析jwt
     */
    public Claims getClaimByToken(String jwt) {
        try {
            return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(jwt)
                .getBody();
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * jwt是否过期
     */
    public boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }
}
