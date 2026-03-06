package id.ac.ui.cs.advprog.authservice.service;

import id.ac.ui.cs.advprog.authservice.dto.RegisterRequest;
import id.ac.ui.cs.advprog.authservice.model.User;
import id.ac.ui.cs.advprog.authservice.repo.InMemoryUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@Service
public class UserService {
    private static final String DEFAULT_ROLE = "user";

    private final InMemoryUserRepository repo;
    private final PasswordEncoder encoder;

    public UserService(InMemoryUserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    public Optional<User> register(RegisterRequest req) {
        if (req == null || !hasText(req.getEmail()) || !hasText(req.getPassword())) {
            return Optional.empty();
        }

        String email = req.getEmail().toLowerCase(Locale.ROOT);
        if (repo.existsByEmail(email)) {
            return Optional.empty();
        }

        String hash = encoder.encode(req.getPassword());
        String role = hasText(req.getRole()) ? req.getRole() : DEFAULT_ROLE;
        User user = new User(req.getName(), email, hash, role);
        repo.save(user);
        return Optional.of(user);
    }

    public Optional<User> findByEmail(String email) {
        if (!hasText(email)) {
            return Optional.empty();
        }

        return repo.findByEmail(email.toLowerCase(Locale.ROOT));
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
