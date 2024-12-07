package pl.tul.discountmanagement.model.response.rest.discount;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PercentageBasedDiscountResponseV1 {
    protected UUID id;
    protected int percentageRate;
}
