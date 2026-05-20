package id.ac.ui.cs.advprog.authservice.security;

import id.ac.ui.cs.advprog.authservice.config.AuthProperties;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class GoogleOAuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final AuthProperties authProperties;

    public GoogleOAuthFailureHandler(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {
        getRedirectStrategy().sendRedirect(
                request,
                response,
                UriComponentsBuilder.fromUriString(authProperties.getFrontendBaseUrl())
                        .path("/login")
                        .queryParam("error", "oauth_google_login_failed")
                        .build()
                        .toUriString()
        );
    }
}
