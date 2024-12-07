package pl.tul.discountmanagement.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.tul.discountmanagement.model.entity.product.ProductEntity;

import java.util.UUID;

/**
 * Interface describing public operations for product persistence.
 */
public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
}
