package id.ac.ui.cs.advprog.authservice.service;

import id.ac.ui.cs.advprog.authservice.dto.RegisterRequest;
import id.ac.ui.cs.advprog.authservice.model.User;
import id.ac.ui.cs.advprog.authservice.repo.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    private UserService userService;

    @Mock
    private InMemoryUserRepository mockRepo;

    @Mock
    private PasswordEncoder mockEncoder;

    @BeforeEach
    void setUp() {
        userService = new UserService(mockRepo, mockEncoder);
    }

    @Test
    @DisplayName("Register success - saves user with encoded password")
    void testRegisterSuccess() {
        RegisterRequest req = new RegisterRequest();
        req.setName("Andi");
        req.setEmail("andi@test.com");
        req.setPassword("password123");
        req.setRole("user");

        when(mockRepo.existsByEmail("andi@test.com")).thenReturn(false);
        when(mockEncoder.encode("password123")).thenReturn("hashed_password");

        Optional<User> result = userService.register(req);

        assertTrue(result.isPresent());
        assertEquals("andi@test.com", result.get().getEmail());
        assertEquals("user", result.get().getRole());
        verify(mockRepo).existsByEmail("andi@test.com");
        verify(mockRepo).save(any(User.class));
        verify(mockEncoder).encode("password123");
    }

    @Test
    @DisplayName("Register duplicate email - fails and does not save")
    void testRegisterDuplicateEmail() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("budi@test.com");
        req.setPassword("password456");

        when(mockRepo.existsByEmail("budi@test.com")).thenReturn(true);

        Optional<User> result = userService.register(req);

        assertTrue(result.isEmpty());
        verify(mockRepo).existsByEmail("budi@test.com");
        verify(mockRepo, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Find by email returns user when present")
    void testFindByEmail() {
        User user = new User("Andi", "andi@test.com", "hashed", "user");
        when(mockRepo.findByEmail("andi@test.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail("andi@test.com");

        assertTrue(result.isPresent());
        assertEquals("andi@test.com", result.get().getEmail());
    }
}
