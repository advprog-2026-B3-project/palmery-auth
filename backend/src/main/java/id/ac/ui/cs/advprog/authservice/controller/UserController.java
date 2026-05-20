package id.ac.ui.cs.advprog.authservice.controller;

import id.ac.ui.cs.advprog.authservice.model.UserAccount;
import id.ac.ui.cs.advprog.authservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANDOR', 'SUPERVISOR', 'SERVICE')")
    public ResponseEntity<List<Map<String, Object>>> listByRole(
            @RequestParam("role") String role) {
        List<UserAccount> accounts = userService.findActiveAccountsByRole(role);
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

    private Map<String, Object> toSummary(UserAccount account) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", account.getId().toString());
        map.put("nama", account.getName());
        map.put("email", account.getEmail());
        map.put("role", account.getRole().getName());
        map.put("kontak", account.getEmail());
        return map;
    }
}
