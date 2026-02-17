package pl.tul.discountmanagement.product.application.mapper;

import jakarta.annotation.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import pl.tul.discountmanagement.product.application.dto.PercentageBasedDiscountDTO;
import pl.tul.discountmanagement.product.application.dto.ProductDTO;
import pl.tul.discountmanagement.product.application.dto.ProductPriceDTO;
import pl.tul.discountmanagement.product.application.dto.QuantityBasedDiscountDTO;
import pl.tul.discountmanagement.product.domain.model.PercentageBasedDiscount;
import pl.tul.discountmanagement.product.domain.model.Product;
import pl.tul.discountmanagement.product.domain.model.QuantityBasedDiscount;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Mapper responsible for converting between domain models and application DTOs.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class ProductDTOMapper {

    public ProductDTO domainToDTO(Product product) {
        return ProductDTO.builder()
                .id(product.id())
                .name(product.name())
                .description(product.description())
                .price(product.price().setScale(product.currency().fractionDigits(), RoundingMode.HALF_UP))
                .currency(product.currency().currencyCode())
                .discounts(mapDiscounts(product))
                .build();
    }

    public ProductPriceDTO toPriceDTO(Product product, @Nullable PercentageBasedDiscount appliedPercentageBasedDiscount,
                                      @Nullable QuantityBasedDiscount appliedQuantityBasedDiscount,
                                      int productQuantity, BigDecimal totalPrice, BigDecimal itemPrice) {
        Set<PercentageBasedDiscountDTO> appliedDiscounts = new LinkedHashSet<>();
        if (nonNull(appliedPercentageBasedDiscount)) {
            appliedDiscounts.add(percentageBasedDiscountToDTO(appliedPercentageBasedDiscount));
        }
        if (nonNull(appliedQuantityBasedDiscount)) {
            appliedDiscounts.add(quantityBasedDiscountToDTO(appliedQuantityBasedDiscount));
        }
        int fractionDigits = product.currency().fractionDigits();
        return ProductPriceDTO.builder()
                .productId(product.id())
                .productQuantity(productQuantity)
                .totalPrice(totalPrice.setScale(fractionDigits, RoundingMode.HALF_UP))
                .itemPrice(itemPrice.setScale(fractionDigits, RoundingMode.HALF_UP))
                .baseItemPrice(product.price().setScale(fractionDigits, RoundingMode.HALF_UP))
                .currency(product.currency().currencyCode())
                .appliedDiscounts(appliedDiscounts)
                .build();
    }

    public PercentageBasedDiscountDTO percentageBasedDiscountToDTO(PercentageBasedDiscount discount) {
        if (discount == null) {
            return null;
        }
        return PercentageBasedDiscountDTO.builder()
                .id(discount.getId())
                .percentageRate(discount.getPercentageRate())
                .build();
    }

    public QuantityBasedDiscountDTO quantityBasedDiscountToDTO(QuantityBasedDiscount discount) {
        if (discount == null) {
            return null;
        }
        return QuantityBasedDiscountDTO.builder()
                .id(discount.getId())
                .percentageRate(discount.getPercentageRate())
                .lowerItemsThreshold(discount.getLowerItemsThreshold())
                .upperItemsThreshold(discount.getUpperItemsThreshold())
                .build();
    }

    private Set<PercentageBasedDiscountDTO> mapDiscounts(Product product) {
        Set<PercentageBasedDiscountDTO> discounts = new LinkedHashSet<>();
        if (nonNull(product.percentageBasedDiscount())) {
            discounts.add(percentageBasedDiscountToDTO(product.percentageBasedDiscount()));
        }
        if (isNull(product.quantityBasedDiscounts())) {
            return discounts;
        }
        product.quantityBasedDiscounts()
                .stream()
                .map(this::quantityBasedDiscountToDTO)
                .forEach(discounts::add);
        return discounts;
    }
}
