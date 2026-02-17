package pl.tul.discountmanagement.product.application.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

/**
 * The DTO class for percentage-based discount data.
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@SuperBuilder
public class PercentageBasedDiscountDTO {

    protected final UUID id;
    protected final int percentageRate;
}
