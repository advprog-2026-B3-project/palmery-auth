package id.ac.ui.cs.advprog.authservice.repo;

import id.ac.ui.cs.advprog.authservice.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {

    Optional<UserAccount> findByEmail(String email);
    boolean existsByEmail(String email);

    List<UserAccount> findByRole_NameAndActiveTrue(String roleName);

    List<UserAccount> findByIdInAndActiveTrue(Collection<UUID> ids);

    @Query("""
            SELECT u FROM UserAccount u
            WHERE u.active = true
              AND (:roleName IS NULL OR u.role.name = :roleName)
              AND (:nameLike IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :nameLike, '%')))
              AND (:emailLike IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :emailLike, '%')))
            ORDER BY u.name ASC
            """)
    List<UserAccount> searchActiveAccounts(
            @Param("roleName") String roleName,
            @Param("nameLike") String nameLike,
            @Param("emailLike") String emailLike);
}
