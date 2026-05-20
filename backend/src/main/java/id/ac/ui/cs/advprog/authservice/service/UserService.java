package id.ac.ui.cs.advprog.authservice.service;

import id.ac.ui.cs.advprog.authservice.dto.RegisterRequest;
import id.ac.ui.cs.advprog.authservice.model.Role;
import id.ac.ui.cs.advprog.authservice.model.User;
import id.ac.ui.cs.advprog.authservice.model.UserAccount;
import id.ac.ui.cs.advprog.authservice.repo.RoleRepository;
import id.ac.ui.cs.advprog.authservice.repo.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.Locale;
import java.util.Optional;

@Service
public class UserService {
    private final UserAccountRepository userAccountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    public UserService(UserAccountRepository userAccountRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder encoder) {
        this.userAccountRepository = userAccountRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
    }

    public Optional<User> register(RegisterRequest req) {
        if (!isValidRegisterRequest(req)) {
            return Optional.empty();
        }

        String email = req.getEmail().toLowerCase(Locale.ROOT).trim();
        if (userAccountRepository.existsByEmail(email)) {
            return Optional.empty();
        }

        String roleName = resolveRoleName(req.getRole());
        Optional<Role> roleOpt = roleRepository.findByName(roleName);
        if (roleOpt.isEmpty()) {
            return Optional.empty();
        }

        String hash = encoder.encode(req.getPassword());

        UserAccount account = new UserAccount(
                req.getName(),
                email,
                hash,
                roleOpt.get()
        );

        userAccountRepository.save(account);

        User user = mapToUser(account);
        return Optional.of(user);
    }

    public Optional<User> findByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }

        String normalized = email.toLowerCase(Locale.ROOT).trim();
        return userAccountRepository.findByEmail(normalized)
                .map(this::mapToUser);
    }

    public Optional<User> findOrCreateOauthUser(String email, String displayName, String requestedRole) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }

        String normalizedEmail = email.toLowerCase(Locale.ROOT).trim();
        Optional<UserAccount> existingAccount = userAccountRepository.findByEmail(normalizedEmail);
        if (existingAccount.isPresent()) {
            return existingAccount.map(this::mapToUser);
        }

        String normalizedName = normalizeDisplayName(displayName, normalizedEmail);
        String roleName = resolveRoleName(requestedRole);
        Optional<Role> roleOpt = roleRepository.findByName(roleName);
        if (roleOpt.isEmpty()) {
            return Optional.empty();
        }

        String passwordHash = encoder.encode(UUID.randomUUID().toString());
        UserAccount account = new UserAccount(
                normalizedName,
                normalizedEmail,
                passwordHash,
                roleOpt.get()
        );

        userAccountRepository.save(account);
        return Optional.of(mapToUser(account));
    }

    private boolean isValidRegisterRequest(RegisterRequest req) {
        if (req == null) {
            return false;
        }
        if (req.getEmail() == null || req.getPassword() == null || req.getName() == null) {
            return false;
        }
        String email = req.getEmail().trim();
        String password = req.getPassword();
        String name = req.getName().trim();

        if (email.isEmpty() || name.isEmpty()) {
            return false;
        }
        // Validasi sederhana panjang password
        return password.length() >= 6;
    }

    private String resolveRoleName(String requestedRole) {
        if (requestedRole == null || requestedRole.isBlank()) {
            // default role untuk registrasi umum adalah WORKER (buruh)
            return "WORKER";
        }
        return requestedRole.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeDisplayName(String displayName, String normalizedEmail) {
        if (displayName != null && !displayName.isBlank()) {
            return displayName.trim();
        }

        int atIndex = normalizedEmail.indexOf('@');
        if (atIndex > 0) {
            return normalizedEmail.substring(0, atIndex);
        }

        return normalizedEmail;
    }

    private User mapToUser(UserAccount account) {
        return new User(
                account.getName(),
                account.getEmail(),
                account.getPasswordHash(),
                account.getRole().getName()
        );
    }
}

