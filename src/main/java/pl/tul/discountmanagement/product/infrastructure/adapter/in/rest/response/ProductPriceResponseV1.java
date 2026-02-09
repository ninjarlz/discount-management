package pl.tul.discountmanagement.product.infrastructure.adapter.in.rest.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

/**
 * The response model representing product price data.
 * API V1
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductPriceResponseV1 {
    private UUID productId;
    private int productQuantity;
    private BigDecimal totalPrice;
    private BigDecimal itemPrice;
    private BigDecimal baseItemPrice;
    private String currency;
    private Set<PercentageBasedDiscountResponseV1> appliedDiscounts;
}
