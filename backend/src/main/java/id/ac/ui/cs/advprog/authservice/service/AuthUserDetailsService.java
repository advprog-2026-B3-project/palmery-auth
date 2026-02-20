package id.ac.ui.cs.advprog.authservice.service;

import id.ac.ui.cs.advprog.authservice.repo.InMemoryUserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthUserDetailsService implements UserDetailsService {

    private final InMemoryUserRepository userRepository;

    public AuthUserDetailsService(InMemoryUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username.toLowerCase())
                .map(user -> new User(
                        user.getEmail(),
                        user.getPasswordHash(),
                        List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase()))
                ))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
