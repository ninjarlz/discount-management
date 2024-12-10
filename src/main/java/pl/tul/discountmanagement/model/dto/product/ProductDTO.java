package pl.tul.discountmanagement.model.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.tul.discountmanagement.model.dto.discount.PercentageBasedDiscountDTO;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

/**
 * The DTO class for product data.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductDTO {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private String currency;
    private Set<PercentageBasedDiscountDTO> discounts;
}
