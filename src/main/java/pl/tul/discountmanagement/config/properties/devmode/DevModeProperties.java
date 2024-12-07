package pl.tul.discountmanagement.config.properties.devmode;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

import static pl.tul.discountmanagement.config.properties.devmode.DevModeProperties.DEV_MODE_PREFIX;

@Getter
@Setter
@ConfigurationProperties(prefix = DEV_MODE_PREFIX)
@Configuration
public class DevModeProperties {

    public static final String DEV_MODE_PREFIX = "dev-mode";
    public static final String MOCK_AUTH_ENABLED_PROPERTY = "mock-auth-enabled";

    private boolean mockAuthEnabled = false;
    private UUID mockAuthUserId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private long mockAuthTokenTimeout = 60L;
}
