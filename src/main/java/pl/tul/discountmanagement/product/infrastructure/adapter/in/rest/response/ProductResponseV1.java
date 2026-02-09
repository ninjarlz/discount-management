package pl.tul.discountmanagement.product.infrastructure.adapter.in.rest.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

/**
 * The response model representing product data.
 * API V1
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductResponseV1 {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private String currency;
    private Set<PercentageBasedDiscountResponseV1> discounts;
}
