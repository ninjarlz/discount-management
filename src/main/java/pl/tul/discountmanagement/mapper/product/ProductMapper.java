package pl.tul.discountmanagement.mapper.product;

import jakarta.annotation.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;
import pl.tul.discountmanagement.mapper.discount.DiscountMapper;
import pl.tul.discountmanagement.model.dto.discount.PercentageBasedDiscountDTO;
import pl.tul.discountmanagement.model.dto.product.ProductDTO;
import pl.tul.discountmanagement.model.dto.product.ProductPriceDTO;
import pl.tul.discountmanagement.model.entity.currency.CurrencyEntity;
import pl.tul.discountmanagement.model.entity.discount.PercentageBasedDiscountEntity;
import pl.tul.discountmanagement.model.entity.discount.QuantityBasedDiscountEntity;
import pl.tul.discountmanagement.model.entity.product.ProductEntity;
import pl.tul.discountmanagement.model.response.rest.product.ProductPriceResponseV1;
import pl.tul.discountmanagement.model.response.rest.product.ProductResponseV1;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Class responsible for mapping instances containing product data.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = DiscountMapper.class)
public abstract class ProductMapper {

    private final DiscountMapper discountMapper = Mappers.getMapper(DiscountMapper.class);

    public ProductDTO entityToDTO(ProductEntity productEntity) {
        CurrencyEntity currencyEntity = productEntity.getCurrency();
        return ProductDTO.builder()
                .id(productEntity.getId())
                .name(productEntity.getName())
                .description(productEntity.getDescription())
                .price(productEntity.getPrice().setScale(currencyEntity.getFractionDigits(), RoundingMode.HALF_UP))
                .currency(currencyEntity.getCurrencyCode())
                .discounts(mapDiscountEntities(productEntity))
                .build();
    }

    public ProductPriceDTO entityToPriceDTO(ProductEntity productEntity, @Nullable PercentageBasedDiscountEntity appliedPercentageBasedDiscountEntity,
                                            @Nullable QuantityBasedDiscountEntity appliedQuantityBasedDiscountEntity,
                                            int productQuantity, BigDecimal totalPrice, BigDecimal itemPrice) {
        Set<PercentageBasedDiscountDTO> appliedDiscounts = new LinkedHashSet<>();
        if (nonNull(appliedPercentageBasedDiscountEntity)) {
            appliedDiscounts.add(discountMapper.percentageBasedDiscountEntityToDTO(appliedPercentageBasedDiscountEntity));
        }
        if (nonNull(appliedQuantityBasedDiscountEntity)) {
            appliedDiscounts.add(discountMapper.quantityBasedDiscountEntityToDTO(appliedQuantityBasedDiscountEntity));
        }
        CurrencyEntity currencyEntity = productEntity.getCurrency();
        return ProductPriceDTO.builder()
                .productId(productEntity.getId())
                .productQuantity(productQuantity)
                .totalPrice(totalPrice.setScale(currencyEntity.getFractionDigits(), RoundingMode.HALF_UP))
                .itemPrice(itemPrice.setScale(currencyEntity.getFractionDigits(), RoundingMode.HALF_UP))
                .baseItemPrice(productEntity.getPrice().setScale(currencyEntity.getFractionDigits(), RoundingMode.HALF_UP))
                .currency(currencyEntity.getCurrencyCode())
                .appliedDiscounts(appliedDiscounts)
                .build();
    }

    public abstract ProductResponseV1 DTOtoResponseV1(ProductDTO productDTO);

    public abstract ProductPriceResponseV1 priceDTOtoPriceResponseV1(ProductPriceDTO productPriceDTO);

    private Set<PercentageBasedDiscountDTO> mapDiscountEntities(ProductEntity productEntity) {
        Set<PercentageBasedDiscountDTO> discounts = new LinkedHashSet<>();
        if (nonNull(productEntity.getPercentageBasedDiscount())) {
            PercentageBasedDiscountDTO percentageBasedDiscountDTO = discountMapper.percentageBasedDiscountEntityToDTO(productEntity.getPercentageBasedDiscount());
            discounts.add(percentageBasedDiscountDTO);
        }
        if (isNull(productEntity.getQuantityBasedDiscounts())) {
            return discounts;
        }
        productEntity.getQuantityBasedDiscounts()
                .stream()
                .map(discountMapper::quantityBasedDiscountEntityToDTO)
                .forEach(discounts::add);
        return discounts;
    }
}
