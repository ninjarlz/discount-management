package pl.tul.discountmanagement.config.properties.devmode;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static pl.tul.discountmanagement.config.properties.devmode.DevModeProperties.DEV_MODE_PREFIX;

@Getter
@Setter
@ConfigurationProperties(prefix = DEV_MODE_PREFIX)
@Configuration
public class DevModeProperties {

    public static final String DEV_MODE_PREFIX = "dev-mode";
    public static final String MOCK_AUTH_ENABLED_PROPERTY = "mock-auth-enabled";

    private boolean mockAuthEnabled = false;
    private long mockAuthUserId = 1L;
    private long mockAuthTokenTimeout = 60L;
}
