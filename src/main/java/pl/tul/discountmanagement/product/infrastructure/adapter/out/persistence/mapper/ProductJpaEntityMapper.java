package pl.tul.discountmanagement.product.infrastructure.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import pl.tul.discountmanagement.product.domain.model.Currency;
import pl.tul.discountmanagement.product.domain.model.PercentageBasedDiscount;
import pl.tul.discountmanagement.product.domain.model.Product;
import pl.tul.discountmanagement.product.domain.model.QuantityBasedDiscount;
import pl.tul.discountmanagement.product.infrastructure.adapter.out.persistence.entity.CurrencyJpaEntity;
import pl.tul.discountmanagement.product.infrastructure.adapter.out.persistence.entity.PercentageBasedDiscountJpaEntity;
import pl.tul.discountmanagement.product.infrastructure.adapter.out.persistence.entity.ProductJpaEntity;
import pl.tul.discountmanagement.product.infrastructure.adapter.out.persistence.entity.QuantityBasedDiscountJpaEntity;

/**
 * Mapper responsible for converting between JPA entities and domain models.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductJpaEntityMapper {

    @Mapping(target = "currency", source = "currency")
    @Mapping(target = "percentageBasedDiscount", source = "percentageBasedDiscount")
    @Mapping(target = "quantityBasedDiscounts", source = "quantityBasedDiscounts")
    Product entityToDomain(ProductJpaEntity entity);

    Currency currencyEntityToDomain(CurrencyJpaEntity entity);

    PercentageBasedDiscount percentageBasedDiscountEntityToDomain(PercentageBasedDiscountJpaEntity entity);

    QuantityBasedDiscount quantityBasedDiscountEntityToDomain(QuantityBasedDiscountJpaEntity entity);
}
