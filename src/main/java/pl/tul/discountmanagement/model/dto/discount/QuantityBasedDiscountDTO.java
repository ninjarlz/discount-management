package pl.tul.discountmanagement.model.dto.discount;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
public class QuantityBasedDiscountDTO extends PercentageBasedDiscountDTO {
    private int lowerItemsThreshold;
    private Integer upperItemsThreshold;
}
