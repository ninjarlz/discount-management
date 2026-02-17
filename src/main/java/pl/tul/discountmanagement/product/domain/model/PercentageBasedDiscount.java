package pl.tul.discountmanagement.product.domain.model;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * Domain value object representing a percentage-based discount.
 * Base type for discount hierarchy (e.g. {@link QuantityBasedDiscount}).
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@SuperBuilder
public class PercentageBasedDiscount {

    protected final UUID id;
    protected final int percentageRate;
}
