package pl.tul.discountmanagement.mapper.product;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;
import pl.tul.discountmanagement.mapper.discount.DiscountMapper;
import pl.tul.discountmanagement.model.dto.discount.PercentageBasedDiscountDTO;
import pl.tul.discountmanagement.model.dto.product.ProductDTO;
import pl.tul.discountmanagement.model.entity.product.ProductEntity;
import pl.tul.discountmanagement.model.response.rest.product.ProductResponseV1;

import java.util.LinkedHashSet;
import java.util.Set;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = DiscountMapper.class)
public abstract class ProductMapper {

    private final DiscountMapper discountMapper = Mappers.getMapper(DiscountMapper.class);

    public ProductDTO entityToDto(ProductEntity productEntity) {
        PercentageBasedDiscountDTO percentageBasedDiscountDTO = discountMapper.percentageBasedDiscountEntityToDTO(productEntity.getPercentageBasedDiscount());
        Set<PercentageBasedDiscountDTO> discounts = new LinkedHashSet<>(Set.of(percentageBasedDiscountDTO));
        productEntity.getQuantityBasedDiscounts().stream()
                .map(discountMapper::quantityBasedDiscountEntityToDTO)
                .forEach(discounts::add);
        return ProductDTO.builder()
                .id(productEntity.getId())
                .name(productEntity.getName())
                .description(productEntity.getDescription())
                .price(productEntity.getPrice())
                .currency(productEntity.getCurrency().getCurrencyCode())
                .discounts(discounts)
                .build();
    }

    public abstract ProductResponseV1 DTOtoResponseV1(ProductDTO productDTO);
}
