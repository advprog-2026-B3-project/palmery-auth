package id.ac.ui.cs.advprog.authservice.security;

import id.ac.ui.cs.advprog.authservice.config.AuthProperties;
import id.ac.ui.cs.advprog.authservice.model.User;
import id.ac.ui.cs.advprog.authservice.service.JwtTokenService;
import id.ac.ui.cs.advprog.authservice.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class GoogleOAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final String DEFAULT_SCOPE = "openid profile email";

    private final AuthProperties authProperties;
    private final UserService userService;
    private final JwtTokenService jwtTokenService;

    public GoogleOAuthSuccessHandler(
            AuthProperties authProperties,
            UserService userService,
            JwtTokenService jwtTokenService
    ) {
        this.authProperties = authProperties;
        this.userService = userService;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        clearAuthenticationAttributes(request);

        try {
            OAuth2User oauthUser = extractOauthUser(authentication);
            String email = readStringAttribute(oauthUser, "email");
            String name = oauthUser.getAttribute("name");

            User user = userService.findOrCreateOauthUser(email, name, authProperties.getOauthDefaultRole())
                    .orElseThrow(() -> new IllegalStateException("Unable to create Google OAuth user"));

            String accessToken = jwtTokenService.generateAccessToken(user, DEFAULT_SCOPE);
            getRedirectStrategy().sendRedirect(request, response, buildFrontendUrl("/oauth-success", "token", accessToken));
        } catch (RuntimeException ex) {
            getRedirectStrategy().sendRedirect(
                    request,
                    response,
                    buildFrontendUrl("/login", "error", "oauth_google_login_failed")
            );
        }
    }

    private OAuth2User extractOauthUser(Authentication authentication) {
        if (!(authentication instanceof OAuth2AuthenticationToken oauthToken)) {
            throw new IllegalStateException("Unsupported authentication type: " + authentication.getClass().getName());
        }
        return oauthToken.getPrincipal();
    }

    private String readStringAttribute(OAuth2User oauthUser, String attributeName) {
        String value = oauthUser.getAttribute(attributeName);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing OAuth attribute: " + attributeName);
        }
        return value;
    }

    private String buildFrontendUrl(String path, String queryName, String queryValue) {
        return UriComponentsBuilder.fromUriString(authProperties.getFrontendBaseUrl())
                .path(path)
                .queryParam(queryName, queryValue)
                .build()
                .toUriString();
    }
}
