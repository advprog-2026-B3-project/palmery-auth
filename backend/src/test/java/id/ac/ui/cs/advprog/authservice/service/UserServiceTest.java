package id.ac.ui.cs.advprog.authservice.service;

import id.ac.ui.cs.advprog.authservice.dto.RegisterRequest;
import id.ac.ui.cs.advprog.authservice.model.Role;
import id.ac.ui.cs.advprog.authservice.model.User;
import id.ac.ui.cs.advprog.authservice.model.UserAccount;
import id.ac.ui.cs.advprog.authservice.repo.RoleRepository;
import id.ac.ui.cs.advprog.authservice.repo.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    private UserService userService;

    @Mock
    private UserAccountRepository mockAccountRepo;

    @Mock
    private RoleRepository mockRoleRepo;

    @Mock
    private PasswordEncoder mockEncoder;

    @BeforeEach
    void setUp() {
        userService = new UserService(mockAccountRepo, mockRoleRepo, mockEncoder);
    }

    @Test
    @DisplayName("Register success - saves user with encoded password")
    void testRegisterSuccess() {
        RegisterRequest req = new RegisterRequest();
        req.setName("Andi");
        req.setEmail("andi@test.com");
        req.setPassword("password123");
        req.setRole("WORKER");

        Role role = new Role("WORKER", "Pekerja / buruh");

        when(mockAccountRepo.existsByEmail("andi@test.com")).thenReturn(false);
        when(mockRoleRepo.findByName("WORKER")).thenReturn(Optional.of(role));
        when(mockEncoder.encode("password123")).thenReturn("hashed_password");

        Optional<User> result = userService.register(req);

        assertTrue(result.isPresent());
        assertEquals("andi@test.com", result.get().getEmail());
        assertEquals("WORKER", result.get().getRole());

        verify(mockAccountRepo).existsByEmail("andi@test.com");
        verify(mockRoleRepo).findByName("WORKER");
        verify(mockEncoder).encode("password123");

        ArgumentCaptor<UserAccount> accountCaptor = ArgumentCaptor.forClass(UserAccount.class);
        verify(mockAccountRepo).save(accountCaptor.capture());
        UserAccount saved = accountCaptor.getValue();
        assertEquals("hashed_password", saved.getPasswordHash());
        assertEquals("andi@test.com", saved.getEmail());
    }

    @Test
    @DisplayName("Register duplicate email - fails and does not save")
    void testRegisterDuplicateEmail() {
        RegisterRequest req = new RegisterRequest();
        req.setName("Budi");
        req.setEmail("budi@test.com");
        req.setPassword("password456");

        when(mockAccountRepo.existsByEmail("budi@test.com")).thenReturn(true);

        Optional<User> result = userService.register(req);

        assertTrue(result.isEmpty());
        verify(mockAccountRepo).existsByEmail("budi@test.com");
        verify(mockAccountRepo, never()).save(any(UserAccount.class));
        verifyNoInteractions(mockRoleRepo);
    }

    @Test
    @DisplayName("Find by email returns user when present")
    void testFindByEmail() {
        Role role = new Role("WORKER", "Pekerja / buruh");
        UserAccount account = new UserAccount(
                "Andi",
                "andi@test.com",
                "hashed",
                role
        );

        when(mockAccountRepo.findByEmail("andi@test.com")).thenReturn(Optional.of(account));

        Optional<User> result = userService.findByEmail("andi@test.com");

        assertTrue(result.isPresent());
        assertEquals("andi@test.com", result.get().getEmail());
    }
}

