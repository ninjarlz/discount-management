package pl.tul.discountmanagement.product.application.dto;

import jakarta.annotation.Nullable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

/**
 * The DTO class for quantity-based discount data.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class QuantityBasedDiscountDTO extends PercentageBasedDiscountDTO {

    private final int lowerItemsThreshold;
    @Nullable
    private final Integer upperItemsThreshold;

    public QuantityBasedDiscountDTO(UUID id, int percentageRate, int lowerItemsThreshold,
                                     @Nullable Integer upperItemsThreshold) {
        super(id, percentageRate);
        this.lowerItemsThreshold = lowerItemsThreshold;
        this.upperItemsThreshold = upperItemsThreshold;
    }
}
