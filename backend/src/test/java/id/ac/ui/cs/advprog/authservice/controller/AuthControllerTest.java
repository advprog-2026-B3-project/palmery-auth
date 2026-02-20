package id.ac.ui.cs.advprog.authservice.controller;

import id.ac.ui.cs.advprog.authservice.config.AuthProperties;
import id.ac.ui.cs.advprog.authservice.dto.RegisterRequest;
import id.ac.ui.cs.advprog.authservice.dto.TokenIntrospectionRequest;
import id.ac.ui.cs.advprog.authservice.dto.TokenRequest;
import id.ac.ui.cs.advprog.authservice.dto.TokenResponse;
import id.ac.ui.cs.advprog.authservice.model.User;
import id.ac.ui.cs.advprog.authservice.service.JwtTokenService;
import id.ac.ui.cs.advprog.authservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Unit Tests")
class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        AuthProperties authProperties = new AuthProperties();
        authProperties.setAccessTokenTtlSeconds(3600);
        authProperties.setServiceClientId("service-a");
        authProperties.setServiceClientSecret("service-secret");

        authController = new AuthController(userService, jwtTokenService, authenticationManager, authProperties);
    }

    @Test
    @DisplayName("Register returns 201 when successful")
    void testRegisterSuccess() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("andi@test.com");
        request.setPassword("password");
        request.setRole("user");

        User user = new User("Andi", "andi@test.com", "hash", "user");
        when(userService.register(request)).thenReturn(Optional.of(user));

        ResponseEntity<Map<String, Object>> response = authController.register(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("registered", response.getBody().get("message"));
    }

    @Test
    @DisplayName("Password grant returns bearer token")
    void testPasswordGrantSuccess() {
        TokenRequest request = new TokenRequest();
        request.setGrant_type("password");
        request.setEmail("andi@test.com");
        request.setPassword("password");
        request.setScope("openid profile email");

        User user = new User("Andi", "andi@test.com", "hash", "user");

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(userService.findByEmail("andi@test.com")).thenReturn(Optional.of(user));
        when(jwtTokenService.generateAccessToken(user, "openid profile email")).thenReturn("jwt-user-token");

        ResponseEntity<?> response = authController.token(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof TokenResponse);
        TokenResponse tokenResponse = (TokenResponse) response.getBody();
        assertEquals("jwt-user-token", tokenResponse.getAccess_token());
        assertEquals("Bearer", tokenResponse.getToken_type());
    }

    @Test
    @DisplayName("Password grant rejects invalid credentials")
    void testPasswordGrantInvalidCredentials() {
        TokenRequest request = new TokenRequest();
        request.setGrant_type("password");
        request.setEmail("andi@test.com");
        request.setPassword("wrong");

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("invalid"));

        ResponseEntity<?> response = authController.token(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Client credentials grant returns bearer token")
    void testClientCredentialsGrantSuccess() {
        TokenRequest request = new TokenRequest();
        request.setGrant_type("client_credentials");
        request.setClient_id("service-a");
        request.setClient_secret("service-secret");
        request.setScope("service.read");

        when(jwtTokenService.generateServiceToken("service-a", "service.read")).thenReturn("jwt-service-token");

        ResponseEntity<?> response = authController.token(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof TokenResponse);
        TokenResponse tokenResponse = (TokenResponse) response.getBody();
        assertEquals("jwt-service-token", tokenResponse.getAccess_token());
    }

    @Test
    @DisplayName("Introspect requires token")
    void testIntrospectRequiresToken() {
        TokenIntrospectionRequest request = new TokenIntrospectionRequest();

        ResponseEntity<?> response = authController.introspect(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Introspect returns payload for valid token")
    void testIntrospectSuccess() {
        TokenIntrospectionRequest request = new TokenIntrospectionRequest();
        request.setToken("valid-token");

        when(jwtTokenService.introspect("valid-token")).thenReturn(Map.of("active", true, "sub", "123"));

        ResponseEntity<?> response = authController.introspect(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Map.of("active", true, "sub", "123"), response.getBody());
    }
}
