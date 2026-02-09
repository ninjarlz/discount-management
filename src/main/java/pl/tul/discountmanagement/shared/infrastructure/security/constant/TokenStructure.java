package pl.tul.discountmanagement.shared.infrastructure.security.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Constant values related to the JWT token structure.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TokenStructure {
    public static final String BEARER_TOKEN_TYPE = "Bearer";
    public static final String TOKEN_TYPE = "at+jwt";
    public static final String SCOPES_CLAIM = "scopes";
    public static final String USER_ID_CLAIM = "userId";
}
