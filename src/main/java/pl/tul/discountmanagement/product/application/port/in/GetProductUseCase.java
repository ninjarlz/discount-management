package pl.tul.discountmanagement.product.application.port.in;

import java.util.UUID;
import pl.tul.discountmanagement.product.application.dto.ProductDTO;
import pl.tul.discountmanagement.product.domain.exception.ProductNotFoundException;

/**
 * Use case for retrieving product details by identifier.
 */
public interface GetProductUseCase {

    /**
     * Return product details for a given product identifier.
     *
     * @param productId given product identifier.
     * @return product details for a given product identifier.
     * @throws ProductNotFoundException when product is not found for given identifier.
     */
    ProductDTO getProductById(UUID productId) throws ProductNotFoundException;
}
