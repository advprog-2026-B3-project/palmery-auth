package id.ac.ui.cs.advprog.authservice.service;

import id.ac.ui.cs.advprog.authservice.config.AuthProperties;
import id.ac.ui.cs.advprog.authservice.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class JwtTokenService {

    private static final String DEFAULT_SCOPE = "openid profile email";

    private final AuthProperties authProperties;
    private final SecretKey signingKey;

    public JwtTokenService(AuthProperties authProperties) {
        this.authProperties = authProperties;
        this.signingKey = buildSigningKey(authProperties.getJwtSecret());
    }

    public String generateAccessToken(User user, String scope) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole());
        claims.put("scope", normalizeScope(scope));

        return buildToken(user.getId(), claims);
    }

    public String generateServiceToken(String clientId, String scope) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("client_id", clientId);
        claims.put("role", "service");
        claims.put("scope", normalizeScope(scope));

        return buildToken(clientId, claims);
    }

    public Optional<Claims> parseClaims(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Optional.of(claims);
        } catch (JwtException | IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    public Map<String, Object> introspect(String token) {
        Map<String, Object> response = new HashMap<>();

        Optional<Claims> optionalClaims = parseClaims(token);
        if (optionalClaims.isEmpty()) {
            response.put("active", false);
            return response;
        }

        Claims claims = optionalClaims.get();
        response.put("active", true);
        response.put("iss", claims.getIssuer());
        response.put("sub", claims.getSubject());
        response.put("email", claims.get("email"));
        response.put("client_id", claims.get("client_id"));
        response.put("role", claims.get("role"));
        response.put("scope", claims.get("scope"));
        if (claims.getExpiration() != null) {
            response.put("exp", claims.getExpiration().toInstant().getEpochSecond());
        }

        return response;
    }

    private String buildToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(authProperties.getAccessTokenTtlSeconds());

        return Jwts.builder()
                .issuer(authProperties.getIssuer())
                .subject(subject)
                .claims(claims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(signingKey)
                .compact();
    }

    private String normalizeScope(String scope) {
        if (scope == null || scope.isBlank()) {
            return DEFAULT_SCOPE;
        }
        return scope;
    }

    private SecretKey buildSigningKey(String configuredSecret) {
        try {
            byte[] decodedKey = Decoders.BASE64.decode(configuredSecret);
            if (decodedKey.length >= 32) {
                return Keys.hmacShaKeyFor(decodedKey);
            }
        } catch (RuntimeException ignored) {
            // Fall back to raw-string key handling.
        }

        byte[] rawKey = configuredSecret.getBytes(StandardCharsets.UTF_8);
        if (rawKey.length < 32) {
            try {
                rawKey = java.security.MessageDigest.getInstance("SHA-256").digest(rawKey);
            } catch (java.security.NoSuchAlgorithmException ex) {
                throw new IllegalStateException("SHA-256 unavailable", ex);
            }
        }
        return Keys.hmacShaKeyFor(rawKey);
    }
}
