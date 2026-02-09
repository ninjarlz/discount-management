package pl.tul.discountmanagement.product.domain.model;

import java.util.UUID;
import lombok.Builder;

/**
 * Domain value object representing a percentage-based discount.
 */
@Builder
public record PercentageBasedDiscount(UUID id, int percentageRate) {}
