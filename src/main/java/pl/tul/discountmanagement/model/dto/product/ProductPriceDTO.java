package pl.tul.discountmanagement.model.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.tul.discountmanagement.model.dto.discount.PercentageBasedDiscountDTO;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

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
