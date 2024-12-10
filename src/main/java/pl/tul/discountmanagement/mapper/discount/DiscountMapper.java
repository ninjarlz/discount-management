package pl.tul.discountmanagement.mapper.discount;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.SubclassMapping;
import pl.tul.discountmanagement.model.dto.discount.PercentageBasedDiscountDTO;
import pl.tul.discountmanagement.model.dto.discount.QuantityBasedDiscountDTO;
import pl.tul.discountmanagement.model.entity.discount.PercentageBasedDiscountEntity;
import pl.tul.discountmanagement.model.entity.discount.QuantityBasedDiscountEntity;
import pl.tul.discountmanagement.model.response.rest.discount.PercentageBasedDiscountResponseV1;
import pl.tul.discountmanagement.model.response.rest.discount.QuantityBasedDiscountResponseV1;

/**
 * Class responsible for mapping instances containing quantity data.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DiscountMapper {
    PercentageBasedDiscountDTO percentageBasedDiscountEntityToDTO(PercentageBasedDiscountEntity percentageBasedDiscountEntity);
    QuantityBasedDiscountDTO quantityBasedDiscountEntityToDTO(QuantityBasedDiscountEntity quantityBasedDiscountEntity);
    @SubclassMapping(source = QuantityBasedDiscountDTO.class, target = QuantityBasedDiscountResponseV1.class)
    PercentageBasedDiscountResponseV1 DTOtoResponse(PercentageBasedDiscountDTO percentageBasedDiscountDTO);
}
