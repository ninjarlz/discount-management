package pl.tul.discountmanagement.util.constant.rest;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Constant values related to the REST API.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApiUrls {
    public static final String API_PREFIX_V1 = "/v1";
    public static final String PRODUCT_ENDPOINT_V1 = API_PREFIX_V1 + "/product";
    public static final String PRICE_PATH_URL = "price";
    public static final String PRODUCT_QUANTITY_REQUEST_PARAMETER = "productQuantity";
}
