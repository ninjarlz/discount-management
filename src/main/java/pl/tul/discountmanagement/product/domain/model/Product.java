package pl.tul.discountmanagement.product.domain.model;

import jakarta.annotation.Nullable;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;

/**
 * Domain entity representing a product with its associated currency and discounts.
 */
@Builder
public record Product(
    UUID id,
    @Nullable String name,
    @Nullable String description,
    BigDecimal price,
    Currency currency,
    @Nullable PercentageBasedDiscount percentageBasedDiscount,
    @Nullable Set<QuantityBasedDiscount> quantityBasedDiscounts
) {}
