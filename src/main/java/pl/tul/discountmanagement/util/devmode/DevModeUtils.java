package pl.tul.discountmanagement.util.devmode;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import pl.tul.discountmanagement.util.constant.config.ApplicationProfiles;

import java.util.Arrays;

/**
 * Util class related to the dev-mode Spring application profile.
 */
@Configuration
@RequiredArgsConstructor
public class DevModeUtils {

    private final Environment environment;

    /**
     * Return boolean indicating whether dev-mode profile is enabled.
     *
     * @return boolean indicating whether dev-mode profile is enabled.
     */
    public boolean isDevModeEnabled() {
        return Arrays.asList(environment.getActiveProfiles()).contains(ApplicationProfiles.DEV_MODE_PROFILE);
    }
}
