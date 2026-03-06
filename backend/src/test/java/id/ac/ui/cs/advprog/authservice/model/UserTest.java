package id.ac.ui.cs.advprog.authservice.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Model Unit Tests")
class UserTest {

    @Test
    @DisplayName("Default constructor assigns id")
    void testDefaultConstructor() {
        User user = new User();

        assertNotNull(user.getId());
        assertFalse(user.getId().isBlank());
    }

    @Test
    @DisplayName("Constructor and setters/getters work")
    void testConstructorAndAccessors() {
        User user = new User("Andi", "andi@test.com", "hash", "user");

        assertNotNull(user.getId());
        assertEquals("Andi", user.getName());
        assertEquals("andi@test.com", user.getEmail());
        assertEquals("hash", user.getPasswordHash());
        assertEquals("user", user.getRole());

        user.setName("Budi");
        user.setEmail("budi@test.com");
        user.setPasswordHash("new-hash");
        user.setRole("admin");

        assertEquals("Budi", user.getName());
        assertEquals("budi@test.com", user.getEmail());
        assertEquals("new-hash", user.getPasswordHash());
        assertEquals("admin", user.getRole());
    }
}
