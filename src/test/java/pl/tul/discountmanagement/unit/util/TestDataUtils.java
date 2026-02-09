package pl.tul.discountmanagement.unit.util;

import jakarta.annotation.Nullable;
import pl.tul.discountmanagement.product.domain.model.Currency;
import pl.tul.discountmanagement.product.domain.model.PercentageBasedDiscount;
import pl.tul.discountmanagement.product.domain.model.Product;
import pl.tul.discountmanagement.product.domain.model.QuantityBasedDiscount;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

/**
 * Util class exposing methods for building test data.
 */
public class TestDataUtils {

    public static Currency buildCurrency(String currencyCode, int fractionDigits) {
        return Currency.builder()
                .id(UUID.randomUUID())
                .currencyCode(currencyCode)
                .fractionDigits(fractionDigits)
                .build();
    }

    public static PercentageBasedDiscount buildPercentageBasedDiscount(int percentageRate) {
        return PercentageBasedDiscount.builder()
                .id(UUID.randomUUID())
                .percentageRate(percentageRate)
                .build();
    }

    public static QuantityBasedDiscount buildQuantityBasedDiscount(int percentageRate, int lowerItemThreshold,
                                                                         @Nullable Integer upperItemThreshold) {
        return QuantityBasedDiscount.builder()
                .id(UUID.randomUUID())
                .percentageRate(percentageRate)
                .lowerItemsThreshold(lowerItemThreshold)
                .upperItemsThreshold(upperItemThreshold)
                .build();
    }

    public static Product buildProduct(UUID productId, BigDecimal productPrice, Currency currency,
                                             @Nullable PercentageBasedDiscount percentageBasedDiscount,
                                             Set<QuantityBasedDiscount> quantityBasedDiscounts) {
        return Product.builder()
                .id(productId)
                .name("NAME")
                .description("DESCRIPTION")
                .price(productPrice)
                .currency(currency)
                .percentageBasedDiscount(percentageBasedDiscount)
                .quantityBasedDiscounts(quantityBasedDiscounts)
                .build();
    }
}
