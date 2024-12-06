package pl.tul.discountmanagement.controller.rest.devmode;

import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.tul.discountmanagement.model.response.devmode.DevModeAccessTokenResponse;
import pl.tul.discountmanagement.service.devmode.DevModeAuthService;

import static org.apache.commons.lang3.BooleanUtils.TRUE;
import static pl.tul.discountmanagement.config.properties.devmode.DevModeProperties.DEV_MODE_PREFIX;
import static pl.tul.discountmanagement.config.properties.devmode.DevModeProperties.MOCK_AUTH_ENABLED_PROPERTY;

@ConditionalOnProperty(prefix = DEV_MODE_PREFIX, name = MOCK_AUTH_ENABLED_PROPERTY, havingValue = TRUE)
@RestController
@RequestMapping(DevModeAuthController.AUTH_URL)
@RequiredArgsConstructor
public class DevModeAuthController {

    public static final String AUTH_URL = "/oauth2";
    private static final String JWKS_URL = "/jwks";
    private static final String ACCESS_TOKEN_URI = "/token";

    private final DevModeAuthService devModeAuthService;

    @GetMapping(value = JWKS_URL, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getJwks() {
        return devModeAuthService.getJwksResponse();
    }

    @PostMapping(value = ACCESS_TOKEN_URI, produces = MediaType.APPLICATION_JSON_VALUE)
    public DevModeAccessTokenResponse getAccessToken() throws JOSEException {
        return devModeAuthService.getAccessTokenResponse();
    }

}
