package pl.tul.discountmanagement.unit.util;

import jakarta.annotation.Nullable;
import pl.tul.discountmanagement.model.entity.currency.CurrencyEntity;
import pl.tul.discountmanagement.model.entity.discount.PercentageBasedDiscountEntity;
import pl.tul.discountmanagement.model.entity.discount.QuantityBasedDiscountEntity;
import pl.tul.discountmanagement.model.entity.product.ProductEntity;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

/**
 * Util class exposing methods for building test data.
 */
public class TestDataUtils {

    public static CurrencyEntity buildCurrencyEntity(String currencyCode, int fractionDigits) {
        return CurrencyEntity.builder()
                .id(UUID.randomUUID())
                .currencyCode(currencyCode)
                .fractionDigits(fractionDigits)
                .build();
    }

    public static PercentageBasedDiscountEntity buildPercentageBasedDiscountEntity(int percentageRate) {
        return PercentageBasedDiscountEntity.builder()
                .id(UUID.randomUUID())
                .percentageRate(percentageRate)
                .build();
    }

    public static QuantityBasedDiscountEntity buildQuantityBasedDiscountEntity(int percentageRate, int lowerItemThreshold,
                                                                         @Nullable Integer upperItemThreshold) {
        return QuantityBasedDiscountEntity.builder()
                .id(UUID.randomUUID())
                .percentageRate(percentageRate)
                .lowerItemsThreshold(lowerItemThreshold)
                .upperItemsThreshold(upperItemThreshold)
                .build();
    }

    public static ProductEntity buildProductEntity(UUID productId, BigDecimal productPrice, CurrencyEntity currency,
                                             @Nullable PercentageBasedDiscountEntity percentageBasedDiscount,
                                             Set<QuantityBasedDiscountEntity> quantityBasedDiscounts) {
        return ProductEntity.builder()
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
