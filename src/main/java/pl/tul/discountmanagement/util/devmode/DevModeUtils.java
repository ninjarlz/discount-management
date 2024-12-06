package pl.tul.discountmanagement.util.devmode;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import pl.tul.discountmanagement.util.constant.config.ApplicationProfiles;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class DevModeUtils {

    private final Environment environment;

    public boolean isDevModeEnabled() {
        return Arrays.asList(environment.getActiveProfiles()).contains(ApplicationProfiles.DEV_MODE_PROFILE);
    }
}
