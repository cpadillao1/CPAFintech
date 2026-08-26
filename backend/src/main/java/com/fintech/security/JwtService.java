package com.fintech.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.expiration.time}")
    private long expirationTime;

    private static final String ISSUER = "fintech-api";
    private static final String CLAIM_ROLES = "roles";

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(secretKey);
    }

    public String createToken(String login, String email, List<String> roles) {
        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(login)
                .withClaim(CLAIM_ROLES, roles)
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                .sign(getAlgorithm());
    }

    public String validateTokenAndGetSubject(String token) {
        try {
            return decodeAndVerify(token).getSubject();
        } catch (Exception e) {
            return null; // Token inválido o expirado
        }
    }

    public DecodedJWT decodeAndVerify(String token) {
        return JWT.require(getAlgorithm())
                .withIssuer(ISSUER)
                .build()
                .verify(token);
    }

    public List<String> getAuthoritiesFromToken(String token) {
        try {
            DecodedJWT decodedJWT = this.decodeAndVerify(token);
            // Extraemos "roles" de forma segura
            List<String> roles = decodedJWT.getClaim(CLAIM_ROLES).asList(String.class);
            return (roles != null) ? roles : Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}