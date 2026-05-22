package id.ac.ui.cs.advprog.authservice.controller;

import id.ac.ui.cs.advprog.authservice.model.UserAccount;
import id.ac.ui.cs.advprog.authservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * GET /api/users
     *   ?role=...  filter by role (legacy)
     *   ?name=...  search by partial name (admin only)
     *   ?email=... search by partial email (admin only)
     *
     * Jika hanya {@code role} yang diisi, perilaku lama dipertahankan agar
     * service lain (palmery-manage / mandor dropdown) tetap bekerja.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANDOR', 'SUPERVISOR', 'SERVICE')")
    public ResponseEntity<List<Map<String, Object>>> listUsers(
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "email", required = false) String email) {

        boolean hasSearchFilter = (name != null && !name.isBlank()) || (email != null && !email.isBlank());
        List<UserAccount> accounts = hasSearchFilter
                ? userService.searchAccounts(name, email, role)
                : userService.findActiveAccountsByRole(role == null ? "WORKER" : role);
        return ResponseEntity.ok(accounts.stream().map(this::toSummary).toList());
    }

    @PostMapping("/by-ids")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANDOR', 'SUPERVISOR', 'SERVICE')")
    public ResponseEntity<List<Map<String, Object>>> listByIds(@RequestBody List<String> ids) {
        List<UUID> uuids = ids.stream()
                .map(id -> {
                    try {
                        return UUID.fromString(id);
                    } catch (IllegalArgumentException ex) {
                        return null;
                    }
                })
                .filter(java.util.Objects::nonNull)
                .toList();

        List<UserAccount> accounts = userService.findActiveAccountsByIds(uuids);
        return ResponseEntity.ok(accounts.stream().map(this::toSummary).toList());
    }

    /**
     * GET /api/users/{id} — detail profil pengguna untuk Admin.
     * Termasuk kolom-kolom spesifik per role (mis. supervisorCertNumber).
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANDOR', 'SUPERVISOR', 'SERVICE')")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable UUID id) {
        return userService.findAccountById(id)
                .map(account -> ResponseEntity.ok(toDetail(account)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * DELETE /api/users/{id} — Admin menghapus user, kecuali dirinya sendiri.
     * Implementasi soft-delete (set active=false) supaya FK di service lain
     * (panen, pengiriman, payroll) tetap konsisten.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        UUID requesterId;
        try {
            requesterId = UUID.fromString(jwt.getSubject());
        } catch (IllegalArgumentException ex) {
            requesterId = null;
        }

        try {
            boolean deactivated = userService.deactivateAccount(id, requesterId);
            if (!deactivated) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    private Map<String, Object> toSummary(UserAccount account) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", account.getId().toString());
        map.put("nama", account.getName());
        map.put("email", account.getEmail());
        map.put("role", account.getRole().getName());
        map.put("kontak", account.getEmail());
        return map;
    }

    private Map<String, Object> toDetail(UserAccount account) {
        Map<String, Object> map = new HashMap<>(toSummary(account));
        map.put("active", account.isActive());
        map.put("supervisorCertNumber", account.getSupervisorCertNumber());
        map.put("createdAt", account.getCreatedAt() == null ? null : account.getCreatedAt().toString());
        map.put("updatedAt", account.getUpdatedAt() == null ? null : account.getUpdatedAt().toString());
        return map;
    }
}
