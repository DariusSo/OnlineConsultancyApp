package com.OnlineConsultancyApp.security;

import com.OnlineConsultancyApp.enums.Categories;
import com.OnlineConsultancyApp.enums.Roles;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

public class JwtGenerator {

    private static final String SECRET_KEY = "mysecret123mysecret123mysecret12345";

    public static String generateJwt(long userId, Roles role) {
        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date exp = new Date(nowMillis + 1120 * 60 * 1000); // 120 minutes

        String jwt = Jwts.builder()
                .setIssuer("manokompanija.eu")
                .setSubject("manokompanija.eu")
                .claim("UserId", userId)
                .claim("Role", role)
                .claim("DateOfLogin", new java.text.SimpleDateFormat("yyyy-MM-dd").format(now))
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return jwt;
    }

}
