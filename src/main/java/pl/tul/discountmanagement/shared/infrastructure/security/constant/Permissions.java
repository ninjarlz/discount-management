package pl.tul.discountmanagement.shared.infrastructure.security.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Constant values related to the REST API authorization.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Permissions {
    // Permissions
    public static final String READ_PRODUCT_PERMISSION = "READ_PRODUCT";
    public static final String READ_PRICE_PERMISSION = "READ_PRICE";

    // Spring security permission expressions
    private static final String HAS_ANY_AUTHORITY_EXPRESSION = "hasAnyAuthority('";
    public static final String READ_PRODUCT_PERMISSION_EXPRESSION = HAS_ANY_AUTHORITY_EXPRESSION + READ_PRODUCT_PERMISSION + "')";
    public static final String READ_PRICE_PERMISSION_EXPRESSION = HAS_ANY_AUTHORITY_EXPRESSION + READ_PRICE_PERMISSION + "')";
}
