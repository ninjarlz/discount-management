package pl.tul.discountmanagement.service.product;

import pl.tul.discountmanagement.exception.product.ProductNotFoundException;
import pl.tul.discountmanagement.model.dto.product.ProductDTO;
import pl.tul.discountmanagement.model.dto.product.ProductPriceDTO;

import java.util.UUID;

/**
 * Service containing business logic for reading product details.
 */
public interface ProductService {

    /**
     * Return product details for a given product identifier.
     *
     * @param productId given product identifier.
     * @return product details for a given product identifier.
     * @throws ProductNotFoundException when product is not found for given identifier.
     */
    ProductDTO getProductById(UUID productId) throws ProductNotFoundException;

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
