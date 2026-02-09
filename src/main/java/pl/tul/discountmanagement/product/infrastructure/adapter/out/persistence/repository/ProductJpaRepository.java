package pl.tul.discountmanagement.product.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.tul.discountmanagement.product.infrastructure.adapter.out.persistence.entity.ProductJpaEntity;

import java.util.UUID;

/**
 * Spring Data JPA repository for product persistence.
 */
@Repository
public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, UUID> {
}
