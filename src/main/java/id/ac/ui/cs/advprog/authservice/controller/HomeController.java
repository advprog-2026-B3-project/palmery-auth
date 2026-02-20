package id.ac.ui.cs.advprog.authservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Auth service running. Use /api/register and /api/login";
    }
}
