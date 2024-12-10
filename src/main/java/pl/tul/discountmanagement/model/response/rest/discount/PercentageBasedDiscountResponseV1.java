package pl.tul.discountmanagement.model.response.rest.discount;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

/**
 * The response model representing percentage based discount data.
 * API V1
 */
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION, defaultImpl = PercentageBasedDiscountResponseV1.class)
@JsonSubTypes(@JsonSubTypes.Type(value = QuantityBasedDiscountResponseV1.class))
public class PercentageBasedDiscountResponseV1 {
    protected UUID id;
    protected int percentageRate;
}
