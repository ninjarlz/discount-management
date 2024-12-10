package pl.tul.discountmanagement.service.product.impl;

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
import pl.tul.discountmanagement.service.product.ProductService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.UUID;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Service containing business logic for reading product details, implementation of {@link ProductService}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private static final int ONE_HUNDRED = 100;
    private static final String PRICE_FORMAT = "%s %s";
    private static final String PRODUCT_FOUND_MSG = "Found product with id '{}'.";
    private static final String PRODUCT_NOT_FOUND_MSG = "Product with id '{}' not found.";
    private static final String PRODUCT_PRICE_CALCULATED_MSG = "Product price calculated for product with id '{}' and quantity '{}', total price is '{}' and item price is '{}'.";
    private static final String PRODUCT_QUANTITY_ERROR_MSG = "Product quantity must be greater than 0.";
    private static final String MATCHING_PERCENTAGE_BASED_DISCOUNT_MSG = "Found matching percentage based discount for product with id '{}' with rate of '{}'%.";
    private static final String MATCHING_QUANTITY_BASED_DISCOUNT_MSG = "Found matching quantity based discount for product with id '{}' with rate of '{}'%.";
    private static final String DISCOUNTS_SUM_MORE_THAN_100_PERCENT_MSG = "Product discounts sum to more than 100%, returning price of zero.";

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    /**
     * Return product details for a given product identifier.
     *
     * @param productId given product identifier.
     * @return product details for a given product identifier.
     * @throws ProductNotFoundException when product is not found for given identifier.
     */
    @Transactional(readOnly = true)
    public ProductDTO getProductById(UUID productId) throws ProductNotFoundException {
        ProductEntity productEntity = getProductEntityById(productId);
        return productMapper.entityToDTO(productEntity);
    }

    /**
     * Calculates a price for given product identifier and product quantity. Applies all associated percentage based
     * discounts and quantity based discounts. Product can be assigned to only one percentage based discount and multiple
     * quantity based discounts. Price can be decreased by applying one percentage based discount and one quantity
     * based discount. In case both types of discounts are assigned to the given product and quantity, their rates are summed up.
     * In case of multiple associated quantity based discounts with overlapping quantity thresholds, the one with higher discount rate
     * is applied.
     *
     * @param productId given product identifier.
     * @param productQuantity given product quantity.
     * @return a price for given product identifier and product quantity.
     * @throws ProductNotFoundException when product is not found for given identifier.
     */
    @Transactional(readOnly = true)
    public ProductPriceDTO calculateProductPrice(UUID productId, int productQuantity) throws ProductNotFoundException {
        throwIfInvalidProductQuantity(productQuantity);
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
        BigDecimal totalPrice = calculateDiscountedTotalPrice(productEntity, productQuantity, discountRate);
        CurrencyEntity currencyEntity = productEntity.getCurrency();
        BigDecimal itemPrice = totalPrice.divide(BigDecimal.valueOf(productQuantity), RoundingMode.HALF_UP)
                .setScale(currencyEntity.getFractionDigits(), RoundingMode.HALF_UP);
        log.info(PRODUCT_PRICE_CALCULATED_MSG, productId, productQuantity, formatPrice(totalPrice, currencyEntity), formatPrice(itemPrice, currencyEntity));
        return productMapper.entityToPriceDTO(productEntity, percentageBasedDiscountEntity, matchingQuantityBasedDiscount,
                productQuantity, totalPrice, itemPrice);

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
        if (isNull(productEntity.getQuantityBasedDiscounts())) {
            return null;
        }
        return productEntity.getQuantityBasedDiscounts()
                .stream()
                .filter(quantityBasedDiscount -> quantityBasedDiscount.getLowerItemsThreshold() <= productQuantity &&
                        (isNull(quantityBasedDiscount.getUpperItemsThreshold()) || quantityBasedDiscount.getUpperItemsThreshold() >= productQuantity))
                .max(Comparator.comparingInt(QuantityBasedDiscountEntity::getPercentageRate))
                .orElse(null);
    }

    private BigDecimal calculateDiscountedTotalPrice(ProductEntity productEntity, int productQuantity, int discountRate) {
        CurrencyEntity currencyEntity = productEntity.getCurrency();
        if (discountRate >= ONE_HUNDRED) {
            log.info(DISCOUNTS_SUM_MORE_THAN_100_PERCENT_MSG);
            return BigDecimal.ZERO.setScale(currencyEntity.getFractionDigits(), RoundingMode.HALF_UP);
        }
        BigDecimal discount = productEntity.getPrice()
                .multiply(BigDecimal.valueOf(productQuantity))
                .multiply(BigDecimal.valueOf(discountRate))
                .scaleByPowerOfTen(-2);
        BigDecimal discountedPrice = productEntity.getPrice()
                .multiply(BigDecimal.valueOf(productQuantity))
                .subtract(discount);
        return discountedPrice.setScale(currencyEntity.getFractionDigits(), RoundingMode.HALF_UP);
    }

    private String formatPrice(BigDecimal price, CurrencyEntity currencyEntity) {
        return PRICE_FORMAT.formatted(price, currencyEntity.getCurrencyCode());
    }

    private void throwIfInvalidProductQuantity(int productQuantity) {
        if (productQuantity > 0) {
            return;
        }
        log.error(PRODUCT_QUANTITY_ERROR_MSG);
        throw new IllegalArgumentException(PRODUCT_QUANTITY_ERROR_MSG);
    }
}
