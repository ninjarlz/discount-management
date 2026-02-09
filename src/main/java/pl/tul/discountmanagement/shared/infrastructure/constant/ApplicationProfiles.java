package pl.tul.discountmanagement.shared.infrastructure.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Constant values related to the supported Spring application profiles.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApplicationProfiles {
    public static final String DEV_MODE_PROFILE = "dev";
    public static final String INTEGRATION_TEST_PROFILE = "integration-test";
}
