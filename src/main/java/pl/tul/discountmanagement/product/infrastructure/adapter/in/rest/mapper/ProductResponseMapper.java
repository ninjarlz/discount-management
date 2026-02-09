package pl.tul.discountmanagement.product.infrastructure.adapter.in.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.SubclassMapping;
import pl.tul.discountmanagement.product.application.dto.PercentageBasedDiscountDTO;
import pl.tul.discountmanagement.product.application.dto.ProductDTO;
import pl.tul.discountmanagement.product.application.dto.ProductPriceDTO;
import pl.tul.discountmanagement.product.application.dto.QuantityBasedDiscountDTO;
import pl.tul.discountmanagement.product.infrastructure.adapter.in.rest.response.PercentageBasedDiscountResponseV1;
import pl.tul.discountmanagement.product.infrastructure.adapter.in.rest.response.ProductPriceResponseV1;
import pl.tul.discountmanagement.product.infrastructure.adapter.in.rest.response.ProductResponseV1;
import pl.tul.discountmanagement.product.infrastructure.adapter.in.rest.response.QuantityBasedDiscountResponseV1;

/**
 * Mapper responsible for converting application DTOs to REST response models.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductResponseMapper {

    ProductResponseV1 DTOtoResponseV1(ProductDTO productDTO);

    ProductPriceResponseV1 priceDTOtoPriceResponseV1(ProductPriceDTO productPriceDTO);

    @SubclassMapping(source = QuantityBasedDiscountDTO.class, target = QuantityBasedDiscountResponseV1.class)
    PercentageBasedDiscountResponseV1 discountDTOtoResponse(PercentageBasedDiscountDTO discountDTO);
}
