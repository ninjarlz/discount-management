package pl.tul.discountmanagement.product.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

/**
 * The DTO class for product data.
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@Builder
public class ProductDTO {

    private final UUID id;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final String currency;
    private final Set<PercentageBasedDiscountDTO> discounts;
}
