package id.ac.ui.cs.advprog.authservice.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("TokenResponse DTO Unit Tests")
class TokenResponseTest {

    @Test
    @DisplayName("Constructor assigns all fields")
    void testConstructorAndGetters() {
        TokenResponse response = new TokenResponse("access-token", "Bearer", 3600, "openid profile");

        assertEquals("access-token", response.getAccess_token());
        assertEquals("Bearer", response.getToken_type());
        assertEquals(3600, response.getExpires_in());
        assertEquals("openid profile", response.getScope());
    }
}
