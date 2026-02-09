package pl.tul.discountmanagement.product.infrastructure.adapter.in.rest.response;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * The response model representing quantity-based discount data.
 * API V1
 */
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class QuantityBasedDiscountResponseV1 extends PercentageBasedDiscountResponseV1 {
    private int lowerItemsThreshold;
    @Nullable
    private Integer upperItemsThreshold;
}
