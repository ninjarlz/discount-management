package pl.tul.discountmanagement.model.dto.discount;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

/**
 * The DTO class for percentage-based discount data.
 */
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION, defaultImpl = PercentageBasedDiscountDTO.class)
@JsonSubTypes(@JsonSubTypes.Type(value = QuantityBasedDiscountDTO.class))
public class PercentageBasedDiscountDTO {
    protected UUID id;
    protected int percentageRate;
}
