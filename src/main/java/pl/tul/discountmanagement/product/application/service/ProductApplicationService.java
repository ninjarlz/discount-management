package pl.tul.discountmanagement.product.application.service;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.tul.discountmanagement.product.application.dto.ProductDTO;
import pl.tul.discountmanagement.product.application.dto.ProductPriceDTO;
import pl.tul.discountmanagement.product.application.mapper.ProductDTOMapper;
import pl.tul.discountmanagement.product.application.port.in.CalculateProductPriceUseCase;
import pl.tul.discountmanagement.product.application.port.in.GetProductUseCase;
import pl.tul.discountmanagement.product.application.port.out.persistence.ProductPersistencePort;
import pl.tul.discountmanagement.product.domain.exception.ProductNotFoundException;
import pl.tul.discountmanagement.product.domain.model.Product;
import pl.tul.discountmanagement.product.domain.model.QuantityBasedDiscount;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.UUID;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Application service implementing product use cases.
 * Orchestrates domain models and persistence port.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductApplicationService implements GetProductUseCase, CalculateProductPriceUseCase {

    private static final int ONE_HUNDRED = 100;
    private static final String PRICE_FORMAT = "%s %s";
    private static final String PRODUCT_FOUND_MSG = "Found product with id '{}'.";
    private static final String PRODUCT_NOT_FOUND_MSG = "Product with id '{}' not found.";
    private static final String PRODUCT_PRICE_CALCULATED_MSG = "Product price calculated for product with id '{}' and quantity '{}', total price is '{}' and item price is '{}'.";
    private static final String PRODUCT_QUANTITY_ERROR_MSG = "Product quantity must be greater than 0.";
    private static final String MATCHING_PERCENTAGE_BASED_DISCOUNT_MSG = "Found matching percentage based discount for product with id '{}' with rate of '{}'%.";
    private static final String MATCHING_QUANTITY_BASED_DISCOUNT_MSG = "Found matching quantity based discount for product with id '{}' with rate of '{}'%.";
    private static final String DISCOUNTS_SUM_EQUALS_TO_OR_MORE_THAN_100_PERCENT_MSG = "Product discounts sum to equals to or more than 100%, returning price of zero.";

    private final ProductPersistencePort productPersistencePort;
    private final ProductDTOMapper productDTOMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public ProductDTO getProductById(UUID productId) throws ProductNotFoundException {
        Product product = getProduct(productId);
        return productDTOMapper.domainToDTO(product);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public ProductPriceDTO calculateProductPrice(UUID productId, int productQuantity) throws ProductNotFoundException {
        throwIfInvalidProductQuantity(productQuantity);
        Product product = getProduct(productId);
        int discountRate = 0;
        var percentageBasedDiscount = product.percentageBasedDiscount();
        if (nonNull(percentageBasedDiscount)) {
            log.info(MATCHING_PERCENTAGE_BASED_DISCOUNT_MSG, productId, percentageBasedDiscount.getPercentageRate());
            discountRate += percentageBasedDiscount.getPercentageRate();
        }
        QuantityBasedDiscount matchingQuantityBasedDiscount = getMatchingQuantityBasedDiscount(product, productQuantity);
        if (nonNull(matchingQuantityBasedDiscount)) {
            log.info(MATCHING_QUANTITY_BASED_DISCOUNT_MSG, productId, matchingQuantityBasedDiscount.getPercentageRate());
            discountRate += matchingQuantityBasedDiscount.getPercentageRate();
        }
        BigDecimal totalPrice = calculateDiscountedTotalPrice(product, productQuantity, discountRate);
        int fractionDigits = product.currency().fractionDigits();
        BigDecimal itemPrice = totalPrice.divide(BigDecimal.valueOf(productQuantity), RoundingMode.HALF_UP)
                .setScale(fractionDigits, RoundingMode.HALF_UP);
        log.info(PRODUCT_PRICE_CALCULATED_MSG, productId, productQuantity,
                formatPrice(totalPrice, product), formatPrice(itemPrice, product));
        return productDTOMapper.toPriceDTO(product, percentageBasedDiscount, matchingQuantityBasedDiscount,
                productQuantity, totalPrice, itemPrice);
    }

    private Product getProduct(UUID productId) throws ProductNotFoundException {
        Product product = productPersistencePort.findById(productId)
                .orElseThrow(() -> {
                    log.error(PRODUCT_NOT_FOUND_MSG, productId);
                    return new ProductNotFoundException(productId);
                });
        log.info(PRODUCT_FOUND_MSG, productId);
        return product;
    }

    @Nullable
    private QuantityBasedDiscount getMatchingQuantityBasedDiscount(Product product, int productQuantity) {
        if (isNull(product.quantityBasedDiscounts())) {
            return null;
        }
        return product.quantityBasedDiscounts()
                .stream()
                .filter(quantityBasedDiscount -> quantityBasedDiscount.getLowerItemsThreshold() <= productQuantity &&
                        (isNull(quantityBasedDiscount.getUpperItemsThreshold()) || quantityBasedDiscount.getUpperItemsThreshold() >= productQuantity))
                .max(Comparator.comparingInt(QuantityBasedDiscount::getPercentageRate))
                .orElse(null);
    }

    private BigDecimal calculateDiscountedTotalPrice(Product product, int productQuantity, int discountRate) {
        int fractionDigits = product.currency().fractionDigits();
        if (discountRate >= ONE_HUNDRED) {
            log.info(DISCOUNTS_SUM_EQUALS_TO_OR_MORE_THAN_100_PERCENT_MSG);
            return BigDecimal.ZERO.setScale(fractionDigits, RoundingMode.HALF_UP);
        }
        BigDecimal totalPrice = product.price()
                .multiply(BigDecimal.valueOf(productQuantity));
        BigDecimal discount = totalPrice
                .multiply(BigDecimal.valueOf(discountRate))
                .scaleByPowerOfTen(-2);
        BigDecimal discountedPrice = totalPrice
                .subtract(discount);
        return discountedPrice.setScale(fractionDigits, RoundingMode.HALF_UP);
    }

    private String formatPrice(BigDecimal price, Product product) {
        return PRICE_FORMAT.formatted(price, product.currency().currencyCode());
    }

    private void throwIfInvalidProductQuantity(int productQuantity) {
        if (productQuantity > 0) {
            return;
        }
        log.error(PRODUCT_QUANTITY_ERROR_MSG);
        throw new IllegalArgumentException(PRODUCT_QUANTITY_ERROR_MSG);
    }
}
