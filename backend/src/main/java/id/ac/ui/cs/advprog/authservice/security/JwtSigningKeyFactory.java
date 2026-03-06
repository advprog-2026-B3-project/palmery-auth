package id.ac.ui.cs.advprog.authservice.security;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class JwtSigningKeyFactory {

    private static final int MIN_KEY_BYTES = 32;

    private JwtSigningKeyFactory() {
        // Utility class.
    }

    public static SecretKey fromSecret(String configuredSecret) {
        try {
            byte[] decodedKey = Decoders.BASE64.decode(configuredSecret);
            if (decodedKey.length >= MIN_KEY_BYTES) {
                return Keys.hmacShaKeyFor(decodedKey);
            }
        } catch (RuntimeException ignored) {
            // Fall back to raw-string key handling.
        }

        byte[] rawKey = configuredSecret.getBytes(StandardCharsets.UTF_8);
        if (rawKey.length < MIN_KEY_BYTES) {
            rawKey = sha256(rawKey);
        }
        return Keys.hmacShaKeyFor(rawKey);
    }

    private static byte[] sha256(byte[] payload) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(payload);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 unavailable", ex);
        }
    }
}
