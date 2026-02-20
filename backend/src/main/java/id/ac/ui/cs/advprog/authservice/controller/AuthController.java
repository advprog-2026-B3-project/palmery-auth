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
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String DEFAULT_OAUTH_SCOPE = "openid profile email";

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
        if (req.getEmail() == null || req.getPassword() == null) {
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
        String grantType = req.getGrant_type() == null || req.getGrant_type().isBlank()
                ? "password"
                : req.getGrant_type().toLowerCase();

        return switch (grantType) {
            case "password" -> issueTokenByPassword(req);
            case "client_credentials" -> issueTokenByClientCredentials(req);
            default -> ResponseEntity.badRequest().body(Map.of(
                    "error", "unsupported_grant_type",
                    "message", "Supported grant_type: password, client_credentials"
            ));
        };
    }

    @PostMapping("/introspect")
    public ResponseEntity<?> introspect(@RequestBody TokenIntrospectionRequest req) {
        if (req.getToken() == null || req.getToken().isBlank()) {
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
        if (req.getEmail() == null || req.getPassword() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "email and password required for grant_type=password"));
        }

        String email = req.getEmail().toLowerCase();

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, req.getPassword()));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "invalid credentials"));
        }

        Optional<User> authenticatedUser = userService.findByEmail(email);
        if (authenticatedUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "invalid credentials"));
        }

        String scope = req.getScope() == null || req.getScope().isBlank() ? DEFAULT_OAUTH_SCOPE : req.getScope();
        String accessToken = jwtTokenService.generateAccessToken(authenticatedUser.get(), scope);

        return ResponseEntity.ok(new TokenResponse(
                accessToken,
                "Bearer",
                authProperties.getAccessTokenTtlSeconds(),
                scope
        ));
    }

    private ResponseEntity<?> issueTokenByClientCredentials(TokenRequest req) {
        if (req.getClient_id() == null || req.getClient_secret() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "client_id and client_secret required for grant_type=client_credentials"));
        }

        boolean validClient = Objects.equals(req.getClient_id(), authProperties.getServiceClientId())
                && Objects.equals(req.getClient_secret(), authProperties.getServiceClientSecret());

        if (!validClient) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "invalid client credentials"));
        }

        String scope = req.getScope() == null || req.getScope().isBlank() ? "service.read service.write" : req.getScope();
        String accessToken = jwtTokenService.generateServiceToken(req.getClient_id(), scope);

        return ResponseEntity.ok(new TokenResponse(
                accessToken,
                "Bearer",
                authProperties.getAccessTokenTtlSeconds(),
                scope
        ));
    }
}
