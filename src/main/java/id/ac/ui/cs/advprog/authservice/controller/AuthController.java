package id.ac.ui.cs.advprog.authservice.controller;

import id.ac.ui.cs.advprog.authservice.dto.LoginRequest;
import id.ac.ui.cs.advprog.authservice.dto.RegisterRequest;
import id.ac.ui.cs.advprog.authservice.model.User;
import id.ac.ui.cs.advprog.authservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (req.getEmail() == null || req.getPassword() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "email and password required"));
        }
        if (userService.register(req).isPresent()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "registered"));
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "email already exists or invalid"));
    }

    @GetMapping("/register")
    public ResponseEntity<?> registerInfo() {
        return ResponseEntity.ok(Map.of(
                "method", "POST",
                "url", "/api/register",
                "body_example", "{\"name\":\"Andi\",\"email\":\"andi@example.com\",\"password\":\"pw\",\"role\":\"user\"}"
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        if (req.getEmail() == null || req.getPassword() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "email and password required"));
        }
        return userService.authenticate(req.getEmail().toLowerCase(), req.getPassword())
                .map(user -> {
                    Map<String,Object> body = new HashMap<>();
                    body.put("message", "ok");
                    body.put("email", user.getEmail());
                    body.put("role", user.getRole());
                    return ResponseEntity.ok(body);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "invalid credentials")));
    }

    @GetMapping("/login")
    public ResponseEntity<?> loginInfo() {
        return ResponseEntity.ok(Map.of(
                "method", "POST",
                "url", "/api/login",
                "body_example", "{\"email\":\"andi@example.com\",\"password\":\"pw\"}"
        ));
    }

}
