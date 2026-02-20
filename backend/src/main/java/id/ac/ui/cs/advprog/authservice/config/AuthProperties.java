package id.ac.ui.cs.advprog.authservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "auth")
public class AuthProperties {

    private String issuer = "http://localhost:8080";
    private String jwtSecret = "replace-this-secret-with-at-least-32-characters";
    private long accessTokenTtlSeconds = 3600;
    private List<String> allowedOrigins = List.of("http://localhost:3000");
    private String serviceClientId = "palmery-internal-service";
    private String serviceClientSecret = "replace-with-service-client-secret";

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getJwtSecret() {
        return jwtSecret;
    }

    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public long getAccessTokenTtlSeconds() {
        return accessTokenTtlSeconds;
    }

    public void setAccessTokenTtlSeconds(long accessTokenTtlSeconds) {
        this.accessTokenTtlSeconds = accessTokenTtlSeconds;
    }

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public String getServiceClientId() {
        return serviceClientId;
    }

    public void setServiceClientId(String serviceClientId) {
        this.serviceClientId = serviceClientId;
    }

    public String getServiceClientSecret() {
        return serviceClientSecret;
    }

    public void setServiceClientSecret(String serviceClientSecret) {
        this.serviceClientSecret = serviceClientSecret;
    }
}
