package id.ac.ui.cs.advprog.authservice.service;

import id.ac.ui.cs.advprog.authservice.config.AuthProperties;
import id.ac.ui.cs.advprog.authservice.model.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtTokenService Unit Tests")
class JwtTokenServiceTest {

    private JwtTokenService createService() {
        AuthProperties properties = new AuthProperties();
        properties.setIssuer("http://localhost:8080");
        properties.setAccessTokenTtlSeconds(3600);
        properties.setJwtSecret("this-is-a-very-long-secret-key-with-at-least-32-characters");
        return new JwtTokenService(properties);
    }

    @Test
    @DisplayName("Generate token then parse claims")
    void testGenerateAndParseClaims() {
        JwtTokenService tokenService = createService();
        User user = new User("Andi", "andi@test.com", "hash", "user");

        String token = tokenService.generateAccessToken(user, "openid profile email");
        Optional<Claims> claims = tokenService.parseClaims(token);

        assertTrue(claims.isPresent());
        assertEquals(user.getId(), claims.get().getSubject());
        assertEquals("andi@test.com", claims.get().get("email"));
        assertEquals("user", claims.get().get("role"));
        assertEquals("http://localhost:8080", claims.get().getIssuer());
    }

    @Test
    @DisplayName("Generate service token includes client id")
    void testGenerateServiceToken() {
        JwtTokenService tokenService = createService();

        String token = tokenService.generateServiceToken("payment-service", "service.read");
        Optional<Claims> claims = tokenService.parseClaims(token);

        assertTrue(claims.isPresent());
        assertEquals("payment-service", claims.get().getSubject());
        assertEquals("payment-service", claims.get().get("client_id"));
        assertEquals("service", claims.get().get("role"));
    }

    @Test
    @DisplayName("Introspect invalid token returns inactive")
    void testIntrospectInvalidToken() {
        JwtTokenService tokenService = createService();

        Map<String, Object> response = tokenService.introspect("invalid-token");

        assertEquals(Boolean.FALSE, response.get("active"));
    }

    @Test
    @DisplayName("Parse claims returns empty for blank token")
    void testParseClaimsBlankToken() {
        JwtTokenService tokenService = createService();

        assertTrue(tokenService.parseClaims(" ").isEmpty());
    }

    @Test
    @DisplayName("Generate access token uses default scope when blank")
    void testGenerateAccessTokenDefaultScope() {
        JwtTokenService tokenService = createService();
        User user = new User("Andi", "andi@test.com", "hash", "user");

        String token = tokenService.generateAccessToken(user, " ");
        Optional<Claims> claims = tokenService.parseClaims(token);

        assertTrue(claims.isPresent());
        assertEquals("openid profile email", claims.get().get("scope"));
    }

    @Test
    @DisplayName("Introspect valid token returns active payload")
    void testIntrospectValidToken() {
        JwtTokenService tokenService = createService();
        User user = new User("Andi", "andi@test.com", "hash", "user");
        String token = tokenService.generateAccessToken(user, "openid profile email");

        Map<String, Object> response = tokenService.introspect(token);

        assertEquals(Boolean.TRUE, response.get("active"));
        assertEquals(user.getId(), response.get("sub"));
        assertEquals("andi@test.com", response.get("email"));
        assertEquals("user", response.get("role"));
        assertNotNull(response.get("exp"));
    }
}
