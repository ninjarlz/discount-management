package pl.tul.discountmanagement.model.response.rest.discount;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class QuantityBasedDiscountResponseV1 extends PercentageBasedDiscountResponseV1 {
    private int lowerItemsThreshold;
    private Integer upperItemsThreshold;
}