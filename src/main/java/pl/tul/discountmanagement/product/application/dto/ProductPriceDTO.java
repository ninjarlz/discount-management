package pl.tul.discountmanagement.product.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

/**
 * The DTO class for product price data.
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@Builder
public class ProductPriceDTO {

    private final UUID productId;
    private final int productQuantity;
    private final BigDecimal totalPrice;
    private final BigDecimal itemPrice;
    private final BigDecimal baseItemPrice;
    private final String currency;
    private final Set<PercentageBasedDiscountDTO> appliedDiscounts;
}
