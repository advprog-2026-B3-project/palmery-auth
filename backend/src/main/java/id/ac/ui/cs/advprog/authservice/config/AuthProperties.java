package id.ac.ui.cs.advprog.authservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "auth")
public class AuthProperties {

    private String issuer = "http://localhost:8080";
    private String jwtSecret = "replace-this-secret-with-at-least-32-characters";
    private long accessTokenTtlSeconds = 3600;
    private List<String> allowedOrigins = List.of("http://localhost:3000");
    private String frontendBaseUrl = "http://localhost:3000";
    private String appBaseUrl = "http://localhost:3001";
    private List<String> allowedReturnOrigins = List.of(
            "http://localhost:3001",
            "http://127.0.0.1:3001"
    );
    private String serviceClientId = "palmery-internal-service";
    private String serviceClientSecret = "replace-with-service-client-secret";
    private boolean googleEnabled;
    private String googleClientId;
    private String googleClientSecret;
    private String oauthDefaultRole = "WORKER";

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

    public String getFrontendBaseUrl() {
        return frontendBaseUrl;
    }

    public void setFrontendBaseUrl(String frontendBaseUrl) {
        this.frontendBaseUrl = frontendBaseUrl;
    }

    public String getAppBaseUrl() {
        return appBaseUrl;
    }

    public void setAppBaseUrl(String appBaseUrl) {
        this.appBaseUrl = appBaseUrl;
    }

    public List<String> getAllowedReturnOrigins() {
        return allowedReturnOrigins;
    }

    public void setAllowedReturnOrigins(List<String> allowedReturnOrigins) {
        this.allowedReturnOrigins = allowedReturnOrigins;
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

    public boolean isGoogleEnabled() {
        return googleEnabled;
    }

    public void setGoogleEnabled(boolean googleEnabled) {
        this.googleEnabled = googleEnabled;
    }

    public String getGoogleClientId() {
        return googleClientId;
    }

    public void setGoogleClientId(String googleClientId) {
        this.googleClientId = googleClientId;
    }

    public String getGoogleClientSecret() {
        return googleClientSecret;
    }

    public void setGoogleClientSecret(String googleClientSecret) {
        this.googleClientSecret = googleClientSecret;
    }

    public String getOauthDefaultRole() {
        return oauthDefaultRole;
    }

    public void setOauthDefaultRole(String oauthDefaultRole) {
        this.oauthDefaultRole = oauthDefaultRole;
    }
}
