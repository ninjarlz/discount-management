package pl.tul.discountmanagement.model.response.rest.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.tul.discountmanagement.model.response.rest.discount.PercentageBasedDiscountResponseV1;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

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
