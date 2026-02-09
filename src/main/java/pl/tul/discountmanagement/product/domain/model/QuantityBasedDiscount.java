package pl.tul.discountmanagement.product.domain.model;

import jakarta.annotation.Nullable;
import java.util.UUID;
import lombok.Builder;

/**
 * Domain value object representing a quantity-based discount.
 */
@Builder
public record QuantityBasedDiscount(UUID id, int lowerItemsThreshold, @Nullable Integer upperItemsThreshold, int percentageRate) {}
