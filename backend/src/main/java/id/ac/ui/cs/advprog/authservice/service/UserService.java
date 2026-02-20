package id.ac.ui.cs.advprog.authservice.service;

import id.ac.ui.cs.advprog.authservice.dto.RegisterRequest;
import id.ac.ui.cs.advprog.authservice.model.User;
import id.ac.ui.cs.advprog.authservice.repo.InMemoryUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final InMemoryUserRepository repo;
    private final PasswordEncoder encoder;

    public UserService(InMemoryUserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    public Optional<User> register(RegisterRequest req) {
        if (req.getEmail() == null || req.getPassword() == null) {
            return Optional.empty();
        }

        String email = req.getEmail().toLowerCase();
        if (repo.existsByEmail(email)) {
            return Optional.empty();
        }

        String hash = encoder.encode(req.getPassword());
        User user = new User(req.getName(), email, hash, req.getRole() == null ? "user" : req.getRole());
        repo.save(user);
        return Optional.of(user);
    }

    public Optional<User> findByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }

        return repo.findByEmail(email.toLowerCase());
    }
}
