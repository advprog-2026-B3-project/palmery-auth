package id.ac.ui.cs.advprog.authservice.service;

import id.ac.ui.cs.advprog.authservice.model.User;
import id.ac.ui.cs.advprog.authservice.repo.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthUserDetailsService Unit Tests")
class AuthUserDetailsServiceTest {

    @Mock
    private InMemoryUserRepository userRepository;

    private AuthUserDetailsService authUserDetailsService;

    @BeforeEach
    void setUp() {
        authUserDetailsService = new AuthUserDetailsService(userRepository);
    }

    @Test
    @DisplayName("Load user by username returns user details")
    void testLoadUserByUsernameSuccess() {
        User user = new User("Andi", "andi@test.com", "hashed", "admin");
        when(userRepository.findByEmail("andi@test.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = authUserDetailsService.loadUserByUsername("andi@test.com");

        assertEquals("andi@test.com", userDetails.getUsername());
        assertEquals("hashed", userDetails.getPassword());
        assertEquals("ROLE_ADMIN", userDetails.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    @DisplayName("Load user throws when missing")
    void testLoadUserByUsernameNotFound() {
        when(userRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> authUserDetailsService.loadUserByUsername("missing@test.com")
        );
    }
}
