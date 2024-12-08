package pl.tul.discountmanagement.service;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.tul.discountmanagement.exception.product.ProductNotFoundException;
import pl.tul.discountmanagement.mapper.product.ProductMapper;
import pl.tul.discountmanagement.model.dto.product.ProductDTO;
import pl.tul.discountmanagement.model.dto.product.ProductPriceDTO;
import pl.tul.discountmanagement.model.entity.discount.PercentageBasedDiscountEntity;
import pl.tul.discountmanagement.model.entity.discount.QuantityBasedDiscountEntity;
import pl.tul.discountmanagement.model.entity.product.ProductEntity;
import pl.tul.discountmanagement.repository.product.ProductRepository;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.UUID;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final String PRODUCT_QUANTITY_ERROR_MSG = "Product quantity must be greater than 0";

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductDTO getProductById(UUID productId) throws ProductNotFoundException {
        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        return productMapper.entityToDto(productEntity);
    }

    public ProductPriceDTO calculateProductPrice(UUID productId, int productQuantity) throws ProductNotFoundException {
        if (productQuantity < 1) {
            throw new IllegalArgumentException(PRODUCT_QUANTITY_ERROR_MSG);
        }
        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        int discountRate = 0;
        PercentageBasedDiscountEntity percentageBasedDiscountEntity = productEntity.getPercentageBasedDiscount();
        if (nonNull(percentageBasedDiscountEntity)) {
            discountRate += productEntity.getPercentageBasedDiscount().getPercentageRate();
        }
        QuantityBasedDiscountEntity matchingQuantityBasedDiscount = getMatchingQuantityBasedDiscount(productEntity, productQuantity);
        if (nonNull(matchingQuantityBasedDiscount)) {
            discountRate += matchingQuantityBasedDiscount.getPercentageRate();
        }
        BigDecimal discountedItemPrice = calculateDiscountedItemPrice(productEntity, discountRate);
        return productMapper.entityToPriceDTO(productEntity, percentageBasedDiscountEntity, matchingQuantityBasedDiscount,
                productQuantity, discountedItemPrice);

    }

    @Nullable
    private QuantityBasedDiscountEntity getMatchingQuantityBasedDiscount(ProductEntity productEntity, int productQuantity) {
        return productEntity.getQuantityBasedDiscounts()
                .stream()
                .filter(quantityBasedDiscount -> quantityBasedDiscount.getLowerItemsThreshold() <= productQuantity &&
                        (isNull(quantityBasedDiscount.getUpperItemsThreshold()) || quantityBasedDiscount.getUpperItemsThreshold() >= productQuantity))
                .max(Comparator.comparingInt(QuantityBasedDiscountEntity::getPercentageRate))
                .orElse(null);
    }

    private BigDecimal calculateDiscountedItemPrice(ProductEntity productEntity, int discountRate) {
        BigDecimal discount = productEntity.getPrice()
                .multiply(BigDecimal.valueOf(discountRate))
                .scaleByPowerOfTen(-2);
        BigDecimal discountedPrice = productEntity.getPrice()
                .subtract(discount);
        if (discountedPrice.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        return discountedPrice;
    }
}
