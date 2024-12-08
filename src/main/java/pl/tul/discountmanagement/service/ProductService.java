package pl.tul.discountmanagement.service;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.tul.discountmanagement.exception.product.ProductNotFoundException;
import pl.tul.discountmanagement.mapper.product.ProductMapper;
import pl.tul.discountmanagement.model.dto.product.ProductDTO;
import pl.tul.discountmanagement.model.dto.product.ProductPriceDTO;
import pl.tul.discountmanagement.model.entity.currency.CurrencyEntity;
import pl.tul.discountmanagement.model.entity.discount.PercentageBasedDiscountEntity;
import pl.tul.discountmanagement.model.entity.discount.QuantityBasedDiscountEntity;
import pl.tul.discountmanagement.model.entity.product.ProductEntity;
import pl.tul.discountmanagement.repository.product.ProductRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.UUID;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private static final String PRODUCT_FOUND_MSG = "Found product with id '{}'.";
    private static final String PRODUCT_NOT_FOUND_MSG = "Product with id '{}' not found.";
    private static final String PRODUCT_PRICE_CALCULATED_MSG = "Product price calculated for product with id '{}' and quantity '{}', discounted item price is '{}'.";
    private static final String PRODUCT_QUANTITY_ERROR_MSG = "Product quantity must be greater than 0.";
    private static final String MATCHING_PERCENTAGE_BASED_DISCOUNT_MSG = "Found matching percentage based discount for product with id '{}' with rate of '{}'%.";
    private static final String MATCHING_QUANTITY_BASED_DISCOUNT_MSG = "Found matching quantity based discount for product with id '{}' with rate of '{}'%.";
    private static final String DISCOUNTS_SUM_MORE_THAN_100_PERCENT_MSG = "Product discounts sum to more than 100%, returning price of zero.";

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public ProductDTO getProductById(UUID productId) throws ProductNotFoundException {
        ProductEntity productEntity = getProductEntityById(productId);
        return productMapper.entityToDTO(productEntity);
    }

    @Transactional(readOnly = true)
    public ProductPriceDTO calculateProductPrice(UUID productId, int productQuantity) throws ProductNotFoundException {
        if (productQuantity < 1) {
            log.error(PRODUCT_QUANTITY_ERROR_MSG);
            throw new IllegalArgumentException(PRODUCT_QUANTITY_ERROR_MSG);
        }
        ProductEntity productEntity = getProductEntityById(productId);
        int discountRate = 0;
        PercentageBasedDiscountEntity percentageBasedDiscountEntity = productEntity.getPercentageBasedDiscount();
        if (nonNull(percentageBasedDiscountEntity)) {
            log.info(MATCHING_PERCENTAGE_BASED_DISCOUNT_MSG, productId, percentageBasedDiscountEntity.getPercentageRate());
            discountRate += productEntity.getPercentageBasedDiscount().getPercentageRate();
        }
        QuantityBasedDiscountEntity matchingQuantityBasedDiscount = getMatchingQuantityBasedDiscount(productEntity, productQuantity);
        if (nonNull(matchingQuantityBasedDiscount)) {
            log.info(MATCHING_QUANTITY_BASED_DISCOUNT_MSG, productId, matchingQuantityBasedDiscount.getPercentageRate());
            discountRate += matchingQuantityBasedDiscount.getPercentageRate();
        }
        BigDecimal discountedItemPrice = calculateDiscountedItemPrice(productEntity, discountRate);
        log.info(PRODUCT_PRICE_CALCULATED_MSG, productId, productQuantity, discountedItemPrice);
        return productMapper.entityToPriceDTO(productEntity, percentageBasedDiscountEntity, matchingQuantityBasedDiscount,
                productQuantity, discountedItemPrice);

    }

    private ProductEntity getProductEntityById(UUID productId) throws ProductNotFoundException {
        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error(PRODUCT_NOT_FOUND_MSG, productId);
                    return new ProductNotFoundException(productId);
                });
        log.info(PRODUCT_FOUND_MSG, productId);
        return productEntity;
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
        CurrencyEntity currencyEntity = productEntity.getCurrency();
        BigDecimal discount = productEntity.getPrice()
                .multiply(BigDecimal.valueOf(discountRate))
                .scaleByPowerOfTen(-2);
        BigDecimal discountedPrice = productEntity.getPrice()
                .subtract(discount);
        if (discountedPrice.compareTo(BigDecimal.ZERO) < 0) {
            log.info(DISCOUNTS_SUM_MORE_THAN_100_PERCENT_MSG);
            return BigDecimal.ZERO.setScale(currencyEntity.getFractionDigits(), RoundingMode.HALF_UP);
        }
        return discountedPrice.setScale(currencyEntity.getFractionDigits(), RoundingMode.HALF_UP);
    }
}
