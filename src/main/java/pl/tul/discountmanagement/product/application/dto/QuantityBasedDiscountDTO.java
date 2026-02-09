package pl.tul.discountmanagement.product.application.dto;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * The DTO class for quantity-based discount data.
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
public class QuantityBasedDiscountDTO extends PercentageBasedDiscountDTO {
    private int lowerItemsThreshold;
    @Nullable
    private Integer upperItemsThreshold;
}
