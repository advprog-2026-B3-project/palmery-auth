package id.ac.ui.cs.advprog.authservice.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/protected")
public class ProtectedController {

    @GetMapping("/ping")
    public Map<String, Object> ping(@AuthenticationPrincipal Jwt jwt) {
        Map<String, Object> res = new HashMap<>();
        res.put("message", "Protected OK");
        res.put("iss", jwt.getIssuer().toString());
        res.put("sub", jwt.getSubject());
        res.put("email", jwt.getClaim("email"));
        res.put("role", jwt.getClaim("role"));
        res.put("scope", jwt.getClaim("scope"));
        return res;
    }
}

