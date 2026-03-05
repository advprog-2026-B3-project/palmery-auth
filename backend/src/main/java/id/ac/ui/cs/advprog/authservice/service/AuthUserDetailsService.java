package id.ac.ui.cs.advprog.authservice.service;

import id.ac.ui.cs.advprog.authservice.repo.UserAccountRepository;
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

    private final UserAccountRepository userAccountRepository;

    public AuthUserDetailsService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String email = username.toLowerCase(Locale.ROOT).trim();

        return userAccountRepository.findByEmail(email)
                .map(account -> new User(
                        account.getEmail(),
                        account.getPasswordHash(),
                        List.of(new SimpleGrantedAuthority("ROLE_" + account.getRole().getName().toUpperCase(Locale.ROOT)))
                ))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}

