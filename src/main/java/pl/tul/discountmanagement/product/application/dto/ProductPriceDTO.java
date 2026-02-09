package pl.tul.discountmanagement.product.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

/**
 * The DTO class for product price data.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductPriceDTO {
    private UUID productId;
    private int productQuantity;
    private BigDecimal totalPrice;
    private BigDecimal itemPrice;
    private BigDecimal baseItemPrice;
    private String currency;
    private Set<PercentageBasedDiscountDTO> appliedDiscounts;
}
