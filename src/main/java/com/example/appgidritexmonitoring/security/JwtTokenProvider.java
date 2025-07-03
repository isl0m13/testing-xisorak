package com.example.appgidritexmonitoring.security;

import com.example.appgidritexmonitoring.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.UUID;

@Component
public class JwtTokenProvider {
    @Value("${jwt.access.key}")
    private String JWT_SECRET_KEY_FOR_ACCESS_TOKEN;
    @Value("${jwt.access.expiration-time}")
    private Long JWT_EXPIRED_TIME_FOR_ACCESS_TOKEN;
    @Value("${jwt.refresh.key}")
    private String JWT_SECRET_KEY_FOR_REFRESH_TOKEN;
    @Value("${jwt.refresh.expiration-time}")
    private Long JWT_EXPIRED_TIME_FOR_REFRESH_TOKEN;

    public String generateAccessToken(User user, Timestamp issuedAt){
        Timestamp expiredDate = new Timestamp(System.currentTimeMillis() + JWT_EXPIRED_TIME_FOR_ACCESS_TOKEN);
        String userId = user.getId().toString();
        String generateUUID = hideUserId(userId);
        return Jwts.builder()
                .setSubject(generateUUID)
                .setIssuedAt(issuedAt)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET_KEY_FOR_ACCESS_TOKEN)
                .compact();
    }

    public String generateRefreshToken(User user) {
        Timestamp issuedAt = new Timestamp(System.currentTimeMillis());
        Timestamp expireDate = new Timestamp(System.currentTimeMillis() + JWT_EXPIRED_TIME_FOR_REFRESH_TOKEN);

        return Jwts.builder()
                .setSubject(hideUserId(user.getId().toString()))
                .setIssuedAt(issuedAt)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET_KEY_FOR_REFRESH_TOKEN)
                .compact();
    }

    public boolean isValidToken(String token, boolean accessToken) {
        try {
            Jwts
                    .parser()
                    .setSigningKey(accessToken ? JWT_SECRET_KEY_FOR_ACCESS_TOKEN : JWT_SECRET_KEY_FOR_REFRESH_TOKEN)
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUserIdFromToken(String token, boolean accessToken) {
        String userID = Jwts
                .parser()
                .setSigningKey(accessToken ? JWT_SECRET_KEY_FOR_ACCESS_TOKEN : JWT_SECRET_KEY_FOR_REFRESH_TOKEN)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        return showUserId(userID);
    }


    private String hideUserId(String userId){
        String generatedUUID = String.valueOf(UUID.randomUUID());
        String generatedUUID2 = String.valueOf(UUID.randomUUID());
        String substring = generatedUUID.substring(0, 18);
        String concat = substring.concat("-");
        String concat2 = concat.concat(generatedUUID2.substring(0, 18));
        String concat3 = concat2.concat("-");
       return concat3.concat(userId);
    }

    private String showUserId(String concat) {
        return concat.substring(38);
    }


}
