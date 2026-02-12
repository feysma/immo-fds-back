package be.feysdigitalservices.immofds.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtConfig(
        String secret,
        long expirationMs,
        long refreshExpirationMs
) {}
