package id.ac.ui.cs.advprog.authservice.service;

import id.ac.ui.cs.advprog.authservice.dto.RegisterRequest;
import id.ac.ui.cs.advprog.authservice.model.User;
import id.ac.ui.cs.advprog.authservice.repo.InMemoryUserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final InMemoryUserRepository repo;
    private final BCryptPasswordEncoder encoder;

    public UserService(InMemoryUserRepository repo, BCryptPasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    public Optional<User> register(RegisterRequest req) {
        if (req.getEmail() == null || req.getPassword() == null) return Optional.empty();
        String email = req.getEmail().toLowerCase();
        if (repo.existsByEmail(email)) return Optional.empty();
        String hash = encoder.encode(req.getPassword());
        User u = new User(req.getName(), email, hash, req.getRole() == null ? "user" : req.getRole());
        repo.save(u);
        return Optional.of(u);
    }

    public Optional<User> authenticate(String email, String password) {
        return repo.findByEmail(email).filter(u -> encoder.matches(password, u.getPasswordHash()));
    }
}
