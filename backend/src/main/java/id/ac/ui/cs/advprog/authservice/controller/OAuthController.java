package id.ac.ui.cs.advprog.authservice.controller;

import id.ac.ui.cs.advprog.authservice.config.AuthProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Controller
public class OAuthController {

    private final AuthProperties authProperties;

    public OAuthController(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    @GetMapping("/auth/google")
    public void startGoogleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!authProperties.isGoogleEnabled()) {
            response.sendRedirect(buildFrontendLoginErrorUrl("google_oauth_not_configured"));
            return;
        }

        response.sendRedirect(request.getContextPath() + "/oauth2/authorization/google");
    }

    private String buildFrontendLoginErrorUrl(String errorCode) {
        return UriComponentsBuilder.fromUriString(authProperties.getFrontendBaseUrl())
                .path("/login")
                .queryParam("error", errorCode)
                .build()
                .toUriString();
    }
}
