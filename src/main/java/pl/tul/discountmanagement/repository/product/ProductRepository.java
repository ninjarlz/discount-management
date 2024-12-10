package pl.tul.discountmanagement.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.tul.discountmanagement.model.entity.product.ProductEntity;

import java.util.UUID;

/**
 * Interface describing public operations for product persistence.
 */
@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
}
