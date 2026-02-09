package pl.tul.discountmanagement.product.application.port.in;

import java.util.UUID;
import pl.tul.discountmanagement.product.application.dto.ProductPriceDTO;
import pl.tul.discountmanagement.product.domain.exception.ProductNotFoundException;

/**
 * Use case for calculating product price with applicable discounts.
 */
public interface CalculateProductPriceUseCase {

    /**
     * Calculates a price for given product identifier and product quantity.
     *
     * @param productId given product identifier.
     * @param productQuantity given product quantity.
     * @return a price for given product identifier and product quantity.
     * @throws ProductNotFoundException when product is not found for given identifier.
     */
    ProductPriceDTO calculateProductPrice(UUID productId, int productQuantity) throws ProductNotFoundException;
}
