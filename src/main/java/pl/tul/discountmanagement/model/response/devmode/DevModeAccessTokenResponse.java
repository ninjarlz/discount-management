package pl.tul.discountmanagement.model.response.devmode;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO class for storing access token response fetched from mocked Authorization Server in a dev-mode.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DevModeAccessTokenResponse {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private long expiresIn;
}
