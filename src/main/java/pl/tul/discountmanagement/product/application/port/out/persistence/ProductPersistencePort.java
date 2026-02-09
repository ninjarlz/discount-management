package pl.tul.discountmanagement.product.application.port.out.persistence;

import java.util.Optional;
import java.util.UUID;
import pl.tul.discountmanagement.product.domain.model.Product;

/**
 * Port for product persistence operations.
 */
public interface ProductPersistencePort {

    /**
     * Find a product by its unique identifier.
     *
     * @param productId the product identifier.
     * @return an Optional containing the product if found, or empty otherwise.
     */
    Optional<Product> findById(UUID productId);
}
