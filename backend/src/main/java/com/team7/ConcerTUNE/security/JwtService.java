package com.team7.ConcerTUNE.security;

import com.team7.ConcerTUNE.entity.AuthRole;
import com.team7.ConcerTUNE.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret-b64:}")
    private String secretKeyB64;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationMs;

    private Key signKey;

    @PostConstruct
    void initKey() {
        if (secretKeyB64 == null || secretKeyB64.isBlank()) {
            throw new IllegalStateException("JWT secret is missing: set jwt.secret-b64 / JWT_SECRET_B64");
        }
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyB64);
        if (keyBytes.length < 32) {
            throw new IllegalStateException("JWT secret must be >= 32 bytes after Base64 decoding.");
        }
        this.signKey = Keys.hmacShaKeyFor(keyBytes);
    }

    private Key getSignInKey() { return signKey; }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> extra = new HashMap<>();
        if (userDetails instanceof User u) {
            extra.put("id", u.getId());
            extra.put("email", u.getEmail());
            extra.put("auth", u.getAuth().name());
        }
        return generateToken(extra);
    }

    public String generateToken(Map<String, Object> extra) {
        return buildToken(extra, jwtExpirationMs);
    }

    public String generateRefreshToken(String email) {
        Map<String, Object> extra = new HashMap<>();
        extra.put("email", email);

        return buildToken(extra, refreshExpirationMs);
    }

    private String buildToken(Map<String, Object> claims, long expMs) {
        long now = System.currentTimeMillis();
        String subjectEmail = (String) claims.get("email");
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subjectEmail)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expMs))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String sub = extractUsername(token);
        final boolean notExpired = extractExpiration(token).after(new Date());
        String expected = userDetails.getUsername();

        return notExpired && sub.equals(expected);
    }


    // 추출관련 메소드

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            throw new CredentialsExpiredException("토큰이 만료되었습니다.", e);
        } catch (Exception e) {
            throw new BadCredentialsException("유효하지 않은 토큰입니다.", e);
        }
    }

    public SimpleUserDetails createSimpleUserDetailsFromClaims(Claims claims) {
        Long userId = claims.get("id", Long.class);
        String email = claims.getSubject();
        String roleString = claims.get("auth", String.class);

        return new SimpleUserDetails(
                userId,
                email,
                roleString
        );
    }
}