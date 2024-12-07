package pl.tul.discountmanagement.model.dto.discount;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
public class PercentageBasedDiscountDTO {
    protected UUID id;
    protected int percentageRate;
}
