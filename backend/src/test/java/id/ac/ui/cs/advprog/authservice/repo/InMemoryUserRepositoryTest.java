package id.ac.ui.cs.advprog.authservice.repo;

import id.ac.ui.cs.advprog.authservice.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InMemoryUserRepository Unit Tests")
class InMemoryUserRepositoryTest {

    private final InMemoryUserRepository repository = new InMemoryUserRepository();

    @Test
    @DisplayName("Save and find user by email case-insensitively")
    void testSaveAndFindByEmail() {
        User user = new User("Andi", "andi@test.com", "hash", "user");

        repository.save(user);

        assertTrue(repository.findByEmail("ANDI@TEST.COM").isPresent());
        assertTrue(repository.existsByEmail("andi@test.com"));
    }

    @Test
    @DisplayName("Blank and null email are handled safely")
    void testBlankAndNullEmail() {
        assertTrue(repository.findByEmail(null).isEmpty());
        assertTrue(repository.findByEmail(" ").isEmpty());
        assertFalse(repository.existsByEmail(null));
        assertFalse(repository.existsByEmail(" "));
    }

    @Test
    @DisplayName("Save ignores user with blank email")
    void testSaveWithBlankEmail() {
        User user = new User("Budi", " ", "hash", "user");

        User saved = repository.save(user);

        assertEquals(user, saved);
        assertFalse(repository.existsByEmail("budi@test.com"));
    }
}
