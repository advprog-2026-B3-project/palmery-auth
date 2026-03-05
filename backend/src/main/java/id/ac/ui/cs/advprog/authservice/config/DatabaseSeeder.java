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
            ensureRole(roleRepository, "SUPERVISOR", "Mandor / supervisor lapangan");
            ensureRole(roleRepository, "WORKER", "Pekerja / buruh");
            ensureRole(roleRepository, "DRIVER", "Supir");

            String adminEmail = "admin@palmery.local";

            if (userAccountRepository.existsByEmail(adminEmail)) {
                return;
            }

            String encodedPassword = passwordEncoder.encode("admin123");

            UserAccount admin = new UserAccount(
                    "Admin Utama",
                    adminEmail,
                    encodedPassword,
                    adminRole
            );

            userAccountRepository.save(admin);
        };
    }

    private Role ensureRole(RoleRepository roleRepository, String name, String description) {
        return roleRepository.findByName(name)
                .orElseGet(() -> roleRepository.save(new Role(name, description)));
    }
}

