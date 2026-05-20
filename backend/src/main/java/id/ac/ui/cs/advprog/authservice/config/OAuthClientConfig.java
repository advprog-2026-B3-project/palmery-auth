package id.ac.ui.cs.advprog.authservice.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;

@Configuration
public class OAuthClientConfig {

    @Bean
    @ConditionalOnProperty(prefix = "auth", name = "google-enabled", havingValue = "true")
    public ClientRegistrationRepository clientRegistrationRepository(AuthProperties authProperties) {
        String clientId = requireValue(authProperties.getGoogleClientId(), "auth.google-client-id");
        String clientSecret = requireValue(authProperties.getGoogleClientSecret(), "auth.google-client-secret");

        ClientRegistration googleRegistration = CommonOAuth2Provider.GOOGLE
                .getBuilder("google")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .scope("openid", "profile", "email")
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .build();

        return new InMemoryClientRegistrationRepository(googleRegistration);
    }

    @Bean
    @ConditionalOnProperty(prefix = "auth", name = "google-enabled", havingValue = "true")
    public OAuth2AuthorizedClientService authorizedClientService(
            ClientRegistrationRepository clientRegistrationRepository
    ) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    private String requireValue(String value, String propertyName) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(propertyName + " must be configured when Google OAuth is enabled");
        }
        return value;
    }
}
