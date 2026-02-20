package id.ac.ui.cs.advprog.authservice.service;

import id.ac.ui.cs.advprog.authservice.dto.RegisterRequest;
import id.ac.ui.cs.advprog.authservice.model.User;
import id.ac.ui.cs.advprog.authservice.repo.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    private UserService userService;

    @Mock
    private InMemoryUserRepository mockRepo;

    @Mock
    private BCryptPasswordEncoder mockEncoder;

    @BeforeEach
    void setUp() {
        userService = new UserService(mockRepo, mockEncoder);
    }

    @Test
    @DisplayName("Register success - should save user when email is unique")
    void testRegisterSuccess() {
        // Arrange
        RegisterRequest req = new RegisterRequest();
        req.setName("Andi");
        req.setEmail("andi@test.com");
        req.setPassword("password123");
        req.setRole("user");

        when(mockRepo.existsByEmail("andi@test.com")).thenReturn(false);
        when(mockEncoder.encode("password123")).thenReturn("hashed_password");

        // Act
        Optional<User> result = userService.register(req);

        // Assert
        assertTrue(result.isPresent(), "Register should return user");
        assertEquals("andi@test.com", result.get().getEmail());
        assertEquals("user", result.get().getRole());
        verify(mockRepo, times(1)).existsByEmail("andi@test.com");
        verify(mockRepo, times(1)).save(any(User.class));
        verify(mockEncoder, times(1)).encode("password123");
    }

    @Test
    @DisplayName("Register duplicate email - should fail when email already exists")
    void testRegisterDuplicateEmail() {
        // Arrange
        RegisterRequest req = new RegisterRequest();
        req.setName("Budi");
        req.setEmail("budi@test.com");
        req.setPassword("password456");
        req.setRole("admin");

        when(mockRepo.existsByEmail("budi@test.com")).thenReturn(true);

        // Act
        Optional<User> result = userService.register(req);

        // Assert
        assertFalse(result.isPresent(), "Register should fail for duplicate email");
        verify(mockRepo, times(1)).existsByEmail("budi@test.com");
        verify(mockRepo, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Login success - should authenticate user with correct password")
    void testLoginSuccess() {
        // Arrange
        User user = new User("Andi", "andi@test.com", "hashed_password", "user");
        when(mockRepo.findByEmail("andi@test.com")).thenReturn(Optional.of(user));
        when(mockEncoder.matches("password123", "hashed_password")).thenReturn(true);

        // Act
        Optional<User> result = userService.authenticate("andi@test.com", "password123");

        // Assert
        assertTrue(result.isPresent(), "Login should succeed with correct password");
        assertEquals("andi@test.com", result.get().getEmail());
        verify(mockRepo, times(1)).findByEmail("andi@test.com");
        verify(mockEncoder, times(1)).matches("password123", "hashed_password");
    }

    @Test
    @DisplayName("Login wrong password - should fail authentication")
    void testLoginWrongPassword() {
        // Arrange
        User user = new User("Andi", "andi@test.com", "hashed_password", "user");
        when(mockRepo.findByEmail("andi@test.com")).thenReturn(Optional.of(user));
        when(mockEncoder.matches("wrongpassword", "hashed_password")).thenReturn(false);

        // Act
        Optional<User> result = userService.authenticate("andi@test.com", "wrongpassword");

        // Assert
        assertFalse(result.isPresent(), "Login should fail with wrong password");
        verify(mockRepo, times(1)).findByEmail("andi@test.com");
        verify(mockEncoder, times(1)).matches("wrongpassword", "hashed_password");
    }
}
