package id.ac.ui.cs.advprog.authservice.repo;

import id.ac.ui.cs.advprog.authservice.model.User;
import org.springframework.stereotype.Repository;

import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryUserRepository {
    private final ConcurrentHashMap<String, User> byEmail = new ConcurrentHashMap<>();

    public Optional<User> findByEmail(String email) {
        return normalizeEmail(email).map(byEmail::get);
    }

    public User save(User user) {
        normalizeEmail(user.getEmail()).ifPresent(email -> byEmail.put(email, user));
        return user;
    }

    public boolean existsByEmail(String email) {
        return normalizeEmail(email).map(byEmail::containsKey).orElse(false);
    }

    private Optional<String> normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(email.toLowerCase(Locale.ROOT));
    }
}
