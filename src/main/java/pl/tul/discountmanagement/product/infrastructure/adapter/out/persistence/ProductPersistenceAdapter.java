package pl.tul.discountmanagement.product.infrastructure.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.tul.discountmanagement.product.application.port.out.persistence.ProductPersistencePort;
import pl.tul.discountmanagement.product.domain.model.Product;
import pl.tul.discountmanagement.product.infrastructure.adapter.out.persistence.mapper.ProductJpaEntityMapper;
import pl.tul.discountmanagement.product.infrastructure.adapter.out.persistence.repository.ProductJpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Adapter implementing the product persistence port using Spring Data JPA.
 */
@Component
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements ProductPersistencePort {

    private final ProductJpaRepository productJpaRepository;
    private final ProductJpaEntityMapper productJpaEntityMapper;

    @Override
    public Optional<Product> findById(UUID productId) {
        return productJpaRepository.findById(productId)
                .map(productJpaEntityMapper::entityToDomain);
    }
}
