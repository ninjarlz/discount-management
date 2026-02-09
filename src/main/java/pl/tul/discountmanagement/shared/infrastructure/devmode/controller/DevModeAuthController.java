package pl.tul.discountmanagement.shared.infrastructure.devmode.controller;

import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.tul.discountmanagement.shared.infrastructure.devmode.config.DevModeProperties;
import pl.tul.discountmanagement.shared.infrastructure.devmode.response.DevModeAccessTokenResponse;
import pl.tul.discountmanagement.shared.infrastructure.devmode.service.DevModeAuthService;

import static org.apache.commons.lang3.BooleanUtils.TRUE;
import static pl.tul.discountmanagement.shared.infrastructure.devmode.config.DevModeProperties.DEV_MODE_PREFIX;
import static pl.tul.discountmanagement.shared.infrastructure.devmode.config.DevModeProperties.MOCK_AUTH_ENABLED_PROPERTY;

/**
 * Class serving as mocked authorization server controller to be used when the dev-mode is enabled.
 * Class is injected into Spring application context if and only if 'dev-mode.mock-auth-enabled' property is set to 'true'.
 * Resulting JWT token structure can be configured via {@link DevModeProperties}.
 */
@ConditionalOnProperty(prefix = DEV_MODE_PREFIX, name = MOCK_AUTH_ENABLED_PROPERTY, havingValue = TRUE)
@RestController
@RequestMapping(DevModeAuthController.AUTH_URL)
@RequiredArgsConstructor
public class DevModeAuthController {

    public static final String AUTH_URL = "/oauth2";
    private static final String JWKS_URL = "/jwks";
    private static final String ACCESS_TOKEN_URI = "/token";

    private final DevModeAuthService devModeAuthService;

    /**
     * Handler for reading JSON Web Key Set to validate incoming JWT tokens.
     */
    @GetMapping(value = JWKS_URL, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getJwks() {
        return devModeAuthService.getJwksResponse();
    }

    /**
     * Handler for reading JWT token containing user credentials defined in {@link DevModeProperties}.
     */
    @PostMapping(value = ACCESS_TOKEN_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public DevModeAccessTokenResponse getAccessToken() throws JOSEException {
        return devModeAuthService.getAccessTokenResponse();
    }
}
