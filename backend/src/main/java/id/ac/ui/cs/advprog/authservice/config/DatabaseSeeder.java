package id.ac.ui.cs.advprog.authservice.config;

import id.ac.ui.cs.advprog.authservice.model.Role;
import id.ac.ui.cs.advprog.authservice.model.UserAccount;
import id.ac.ui.cs.advprog.authservice.repo.RoleRepository;
import id.ac.ui.cs.advprog.authservice.repo.UserAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DatabaseSeeder {

    @Bean
    CommandLineRunner seedInitialData(
            RoleRepository roleRepository,
            UserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            Role adminRole = ensureRole(roleRepository, "ADMIN", "Admin utama sistem");
            Role supervisorRole = ensureRole(roleRepository, "SUPERVISOR", "Mandor / supervisor lapangan");
            Role workerRole = ensureRole(roleRepository, "WORKER", "Pekerja / buruh");
            Role driverRole = ensureRole(roleRepository, "DRIVER", "Supir");

            ensureUser(userAccountRepository, passwordEncoder, "Admin Utama", "admin@palmery.local", "admin123", adminRole);
            ensureUser(userAccountRepository, passwordEncoder, "Mandor Test", "mandor@palmery.local", "mandor123", supervisorRole);
            ensureUser(userAccountRepository, passwordEncoder, "Supir Test", "supir@palmery.local", "supir123", driverRole);
            ensureUser(userAccountRepository, passwordEncoder, "Buruh Test", "buruh@palmery.local", "buruh123", workerRole);
        };
    }

    private Role ensureRole(RoleRepository roleRepository, String name, String description) {
        return roleRepository.findByName(name)
                .orElseGet(() -> roleRepository.save(new Role(name, description)));
    }

    private void ensureUser(UserAccountRepository userAccountRepository,
                            PasswordEncoder passwordEncoder,
                            String name,
                            String email,
                            String password,
                            Role role) {
        if (userAccountRepository.existsByEmail(email)) {
            return;
        }
        userAccountRepository.save(new UserAccount(
                name,
                email,
                passwordEncoder.encode(password),
                role
        ));
    }
}
