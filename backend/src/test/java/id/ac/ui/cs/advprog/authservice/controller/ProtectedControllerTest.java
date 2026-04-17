package id.ac.ui.cs.advprog.authservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.net.URI;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProtectedController Unit Tests")
class ProtectedControllerTest {

    @Mock
    private Jwt jwt;

    private ProtectedController protectedController;

    @BeforeEach
    void setUp() {
        protectedController = new ProtectedController();
    }

    @Test
    @DisplayName("ping returns claims from JWT")
    void testPingSuccess() throws Exception {
        // Mocking behavior based on ProtectedController usage
        when(jwt.getIssuer()).thenReturn(URI.create("http://localhost:8080").toURL());
        when(jwt.getSubject()).thenReturn("user-123");
        when(jwt.getClaim("email")).thenReturn("test@example.com");
        when(jwt.getClaim("role")).thenReturn("ADMIN");
        when(jwt.getClaim("scope")).thenReturn("openid profile email");

        Map<String, Object> result = protectedController.ping(jwt);

        assertEquals("Protected OK", result.get("message"));
        assertEquals("http://localhost:8080", result.get("iss"));
        assertEquals("user-123", result.get("sub"));
        assertEquals("test@example.com", result.get("email"));
        assertEquals("ADMIN", result.get("role"));
        assertEquals("openid profile email", result.get("scope"));
    }
}
