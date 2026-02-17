package pl.tul.discountmanagement.product.domain.model;

import jakarta.annotation.Nullable;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * Domain value object representing a quantity-based discount.
 * Extends {@link PercentageBasedDiscount} with quantity thresholds.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class QuantityBasedDiscount extends PercentageBasedDiscount {

    private final int lowerItemsThreshold;
    @Nullable
    private final Integer upperItemsThreshold;

    public QuantityBasedDiscount(UUID id, int percentageRate, int lowerItemsThreshold,
                                 @Nullable Integer upperItemsThreshold) {
        super(id, percentageRate);
        this.lowerItemsThreshold = lowerItemsThreshold;
        this.upperItemsThreshold = upperItemsThreshold;
    }
}
