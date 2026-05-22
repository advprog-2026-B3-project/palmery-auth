package id.ac.ui.cs.advprog.authservice.repo;

import id.ac.ui.cs.advprog.authservice.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByName(String name);
}

