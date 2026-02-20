package id.ac.ui.cs.advprog.authservice.repo;

import id.ac.ui.cs.advprog.authservice.model.User;
import org.springframework.stereotype.Repository;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Optional;

@Repository
public class InMemoryUserRepository {
    private final ConcurrentHashMap<String, User> byEmail = new ConcurrentHashMap<>();

    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(byEmail.get(email.toLowerCase()));
    }

    public User save(User user) {
        byEmail.put(user.getEmail().toLowerCase(), user);
        return user;
    }

    public boolean existsByEmail(String email) {
        return byEmail.containsKey(email.toLowerCase());
    }
}
