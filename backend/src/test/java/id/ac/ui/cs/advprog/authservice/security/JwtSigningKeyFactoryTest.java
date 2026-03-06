package id.ac.ui.cs.advprog.authservice.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("JwtSigningKeyFactory Unit Tests")
class JwtSigningKeyFactoryTest {

    @Test
    @DisplayName("Build key from base64 secret")
    void testFromSecretWithBase64Secret() {
        byte[] raw = "12345678901234567890123456789012".getBytes(StandardCharsets.UTF_8);
        String base64Secret = Base64.getEncoder().encodeToString(raw);

        SecretKey key = JwtSigningKeyFactory.fromSecret(base64Secret);

        assertArrayEquals(raw, key.getEncoded());
    }

    @Test
    @DisplayName("Build key from short raw secret using SHA-256 fallback")
    void testFromSecretWithShortRawSecret() {
        SecretKey key = JwtSigningKeyFactory.fromSecret("short-secret");

        assertTrue(key.getEncoded().length >= 32);
    }

    @Test
    @DisplayName("Build key from long raw secret")
    void testFromSecretWithLongRawSecret() {
        String secret = "this-is-a-very-long-secret-key-with-at-least-32-characters";

        SecretKey key = JwtSigningKeyFactory.fromSecret(secret);

        assertArrayEquals(secret.getBytes(StandardCharsets.UTF_8), key.getEncoded());
    }

    @Test
    @DisplayName("Build key from short base64 secret falls back to SHA-256")
    void testFromSecretWithShortBase64Secret() {
        SecretKey key = JwtSigningKeyFactory.fromSecret("YWJjZA==");

        assertTrue(key.getEncoded().length >= 32);
    }
}
