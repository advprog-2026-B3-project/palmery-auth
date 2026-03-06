package id.ac.ui.cs.advprog.authservice.controller;

import id.ac.ui.cs.advprog.authservice.config.AuthProperties;
import id.ac.ui.cs.advprog.authservice.dto.RegisterRequest;
import id.ac.ui.cs.advprog.authservice.dto.TokenIntrospectionRequest;
import id.ac.ui.cs.advprog.authservice.dto.TokenRequest;
import id.ac.ui.cs.advprog.authservice.dto.TokenResponse;
import id.ac.ui.cs.advprog.authservice.model.User;
import id.ac.ui.cs.advprog.authservice.service.JwtTokenService;
import id.ac.ui.cs.advprog.authservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String PASSWORD_GRANT_TYPE = "password";
    private static final String CLIENT_CREDENTIALS_GRANT_TYPE = "client_credentials";
    private static final String BEARER_TOKEN_TYPE = "Bearer";
    private static final String INVALID_CREDENTIALS_MESSAGE = "invalid credentials";
    private static final String DEFAULT_OAUTH_SCOPE = "openid profile email";
    private static final String DEFAULT_SERVICE_SCOPE = "service.read service.write";

    private final UserService userService;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;
    private final AuthProperties authProperties;

    public AuthController(
            UserService userService,
            JwtTokenService jwtTokenService,
            AuthenticationManager authenticationManager,
            AuthProperties authProperties
    ) {
        this.userService = userService;
        this.jwtTokenService = jwtTokenService;
        this.authenticationManager = authenticationManager;
        this.authProperties = authProperties;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest req) {
        if (req == null || !hasText(req.getEmail()) || !hasText(req.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("message", "email and password required"));
        }

        Optional<User> user = userService.register(req);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "email already exists or invalid request"));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "registered");
        response.put("email", user.get().getEmail());
        response.put("role", user.get().getRole());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/token")
    public ResponseEntity<?> token(@RequestBody TokenRequest req) {
        if (req == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "request body required"));
        }

        String grantType = normalizeGrantType(req.getGrant_type());

        return switch (grantType) {
            case PASSWORD_GRANT_TYPE -> issueTokenByPassword(req);
            case CLIENT_CREDENTIALS_GRANT_TYPE -> issueTokenByClientCredentials(req);
            default -> ResponseEntity.badRequest().body(Map.of(
                    "error", "unsupported_grant_type",
                    "message", "Supported grant_type: password, client_credentials"
            ));
        };
    }

    @PostMapping("/introspect")
    public ResponseEntity<?> introspect(@RequestBody TokenIntrospectionRequest req) {
        if (req == null || !hasText(req.getToken())) {
            return ResponseEntity.badRequest().body(Map.of("message", "token required"));
        }

        return ResponseEntity.ok(jwtTokenService.introspect(req.getToken()));
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        return ResponseEntity.ok(Map.of(
                "register", Map.of("method", "POST", "url", "/api/auth/register"),
                "token", Map.of(
                        "method", "POST",
                        "url", "/api/auth/token",
                        "grant_types", new String[]{"password", "client_credentials"},
                        "response", "OAuth2-style access token"
                ),
                "introspect", Map.of("method", "POST", "url", "/api/auth/introspect", "response", "RFC7662-like token status"),
                "issuer", authProperties.getIssuer()
        ));
    }

    private ResponseEntity<?> issueTokenByPassword(TokenRequest req) {
        if (!hasText(req.getEmail()) || !hasText(req.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("message", "email and password required for grant_type=password"));
        }

        String email = req.getEmail().toLowerCase(Locale.ROOT);

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, req.getPassword()));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", INVALID_CREDENTIALS_MESSAGE));
        }

        Optional<User> authenticatedUser = userService.findByEmail(email);
        if (authenticatedUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", INVALID_CREDENTIALS_MESSAGE));
        }

        String scope = req.getScope() == null || req.getScope().isBlank() ? DEFAULT_OAUTH_SCOPE : req.getScope();
        String accessToken = jwtTokenService.generateAccessToken(authenticatedUser.get(), scope);

        return ResponseEntity.ok(new TokenResponse(
                accessToken,
                BEARER_TOKEN_TYPE,
                authProperties.getAccessTokenTtlSeconds(),
                scope
        ));
    }

    private ResponseEntity<?> issueTokenByClientCredentials(TokenRequest req) {
        if (!hasText(req.getClient_id()) || !hasText(req.getClient_secret())) {
            return ResponseEntity.badRequest().body(Map.of("message", "client_id and client_secret required for grant_type=client_credentials"));
        }

        boolean validClient = Objects.equals(req.getClient_id(), authProperties.getServiceClientId())
                && Objects.equals(req.getClient_secret(), authProperties.getServiceClientSecret());

        if (!validClient) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "invalid client credentials"));
        }

        String scope = req.getScope() == null || req.getScope().isBlank() ? DEFAULT_SERVICE_SCOPE : req.getScope();
        String accessToken = jwtTokenService.generateServiceToken(req.getClient_id(), scope);

        return ResponseEntity.ok(new TokenResponse(
                accessToken,
                BEARER_TOKEN_TYPE,
                authProperties.getAccessTokenTtlSeconds(),
                scope
        ));
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static String normalizeGrantType(String grantType) {
        if (!hasText(grantType)) {
            return PASSWORD_GRANT_TYPE;
        }
        return grantType.toLowerCase(Locale.ROOT);
    }
}
