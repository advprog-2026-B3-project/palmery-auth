package id.ac.ui.cs.advprog.authservice.service;

import id.ac.ui.cs.advprog.authservice.repo.InMemoryUserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class AuthUserDetailsService implements UserDetailsService {

    private final InMemoryUserRepository userRepository;

    public AuthUserDetailsService(InMemoryUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null || username.isBlank()) {
            throw new UsernameNotFoundException("User not found");
        }

        String normalizedUsername = username.toLowerCase(Locale.ROOT);
        return userRepository.findByEmail(normalizedUsername)
                .map(user -> new User(
                        user.getEmail(),
                        user.getPasswordHash(),
                        List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase(Locale.ROOT)))
                ))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
