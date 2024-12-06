package pl.tul.discountmanagement.util.constant.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TokenStructure {
    public static final String BEARER_TOKEN_TYPE = "Bearer";
    public static final String TOKEN_TYPE = "at+jwt";
    public static final String SCOPES_CLAIM = "scopes";
    public static final String USER_ID_CLAIM = "userId";
}
