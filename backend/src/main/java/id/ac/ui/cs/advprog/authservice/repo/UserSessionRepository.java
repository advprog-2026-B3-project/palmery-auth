package id.ac.ui.cs.advprog.authservice.repo;

import id.ac.ui.cs.advprog.authservice.model.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {

    Optional<UserSession> findBySessionToken(String sessionToken);
}

