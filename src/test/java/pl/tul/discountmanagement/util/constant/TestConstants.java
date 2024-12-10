package pl.tul.discountmanagement.util.constant;

import java.util.UUID;

/**
 * Constant values related to test scenarios.
 */
public final class TestConstants {
    // LOG MESSAGES
    public static final String PRODUCT_FOUND_LOG_MSG = "Found product with id '%s'.";
    public static final String PRODUCT_NOT_FOUND_LOG_MSG = "Product with id '%s' not found.";
    public static final String PRODUCT_PRICE_CALCULATED_LOG_MSG = "Product price calculated for product with id '%s' and quantity '%s', total price is '%s' and item price is '%s'.";
    public static final String PRODUCT_QUANTITY_ERROR_LOG_MSG = "Product quantity must be greater than 0.";
    public static final String MATCHING_PERCENTAGE_BASED_DISCOUNT_LOG_MSG = "Found matching percentage based discount for product with id '%s' with rate of '%d'%%.";
    public static final String MATCHING_QUANTITY_BASED_DISCOUNT_LOG_MSG = "Found matching quantity based discount for product with id '%s' with rate of '%d'%%.";
    public static final String DISCOUNTS_SUM_MORE_THAN_100_PERCENT_LOG_MSG = "Product discounts sum to more than 100%, returning price of zero.";

    // INTEGRATION TEST DATA IDS
    public static final UUID PRODUCT_ID = UUID.fromString("80280a99-7426-4e8d-9706-0387e754d790");
    public static final UUID PERCENTAGE_BASED_DISCOUNT_ID = UUID.fromString("93a92164-a1d0-4c15-aaeb-2022d4b31440");
    public static final UUID QUANTITY_BASED_DISCOUNT_ID_1 = UUID.fromString("1b49c8fc-f01c-4f1d-aff6-402b9c4d5abf");
    public static final UUID QUANTITY_BASED_DISCOUNT_ID_2 = UUID.fromString("0d4731ab-4697-46b9-b7d6-f99f6d20096e");

    // API ERROR RESPONSE MESSAGES
    public static final String DETAIL_ERROR_ENTRY = "detail";
    public static final String MESSAGE_ERROR_ENTRY = "message";
    public static final String PRODUCT_NOT_FOUND_RESPONSE_MSG = "Product with id '%s' not found";
    public static final String INVALID_PRODUCT_ID_RESPONSE_MSG = "Failed to convert 'productId' with value: '%s'";
    public static final String INVALID_PRODUCT_QUANTITY_RESPONSE_MSG = "productQuantity - must be greater than or equal to 1; ";
}
