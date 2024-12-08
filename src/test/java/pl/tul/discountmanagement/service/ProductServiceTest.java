package pl.tul.discountmanagement.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import jakarta.annotation.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import pl.tul.discountmanagement.exception.product.ProductNotFoundException;
import pl.tul.discountmanagement.logging.MemoryAppender;
import pl.tul.discountmanagement.mapper.product.ProductMapper;
import pl.tul.discountmanagement.model.dto.product.ProductDTO;
import pl.tul.discountmanagement.model.dto.product.ProductPriceDTO;
import pl.tul.discountmanagement.model.entity.currency.CurrencyEntity;
import pl.tul.discountmanagement.model.entity.discount.PercentageBasedDiscountEntity;
import pl.tul.discountmanagement.model.entity.discount.QuantityBasedDiscountEntity;
import pl.tul.discountmanagement.model.entity.product.ProductEntity;
import pl.tul.discountmanagement.repository.product.ProductRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pl.tul.discountmanagement.util.TestDataUtils.buildCurrencyEntity;
import static pl.tul.discountmanagement.util.TestDataUtils.buildPercentageBasedDiscountEntity;
import static pl.tul.discountmanagement.util.TestDataUtils.buildProductEntity;
import static pl.tul.discountmanagement.util.TestDataUtils.buildQuantityBasedDiscountEntity;

/**
 * Test class for {@link ProductService}
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    private static final String PRODUCT_FOUND_MSG = "Found product with id '%s'.";
    private static final String PRODUCT_NOT_FOUND_MSG = "Product with id '%s' not found.";
    private static final String PRODUCT_PRICE_CALCULATED_MSG = "Product price calculated for product with id '%s' and quantity '%s', discounted item price is '%s'.";
    private static final String PRODUCT_QUANTITY_ERROR_MSG = "Product quantity must be greater than 0.";
    private static final String MATCHING_PERCENTAGE_BASED_DISCOUNT_MSG = "Found matching percentage based discount for product with id '%s' with rate of '%d'%%.";
    private static final String MATCHING_QUANTITY_BASED_DISCOUNT_MSG = "Found matching quantity based discount for product with id '%s' with rate of '%d'%%.";
    private static final String DISCOUNTS_SUM_MORE_THAN_100_PERCENT_MSG = "Product discounts sum to more than 100%, returning price of zero.";

    private static MemoryAppender memoryAppender;

    @Mock
    private ProductRepository productRepository;

    @Spy
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    /**
     * Setup logger.
     */
    @BeforeAll
    static void setUpLogger() {
        Logger logger = (Logger) LoggerFactory.getLogger(ProductService.class);
        memoryAppender = new MemoryAppender();
        memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        logger.setLevel(Level.INFO);
        logger.addAppender(memoryAppender);
        memoryAppender.start();
    }

    /**
     * Clear logger.
     */
    @AfterEach
    void clearLogger() {
        memoryAppender.reset();
    }

    /**
     * Test purpose         - Verify if {@link ProductService#getProductById(UUID)}
     *                        returns {@link ProductDTO} with proper data.
     * Test data            - product id and product price.
     * Test expected result - An instance of {@link ProductDTO} with proper data is returned.
     * Test type            - Positive.
     */
    @Test
    void getProductById_shouldReturnProduct_whenProductIsFound() throws Exception {
        // Given
        UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        BigDecimal productPrice = new BigDecimal("1000.00");
        CurrencyEntity currencyEntity = buildCurrencyEntity("EUR", 2);
        PercentageBasedDiscountEntity percentageBasedDiscountEntity = buildPercentageBasedDiscountEntity(10);
        Set<QuantityBasedDiscountEntity> quantityBasedDiscountEntities = Set.of(
          buildQuantityBasedDiscountEntity(15, 3, 5),
          buildQuantityBasedDiscountEntity(20, 6, null)
        );
        ProductEntity productEntity = buildProductEntity(productId, productPrice, currencyEntity,
                percentageBasedDiscountEntity, quantityBasedDiscountEntities);
        when(productRepository.findById(eq(productId))).thenReturn(Optional.of(productEntity));

        // When
        ProductDTO productDTO = productService.getProductById(productId);

        // Then
        assertNotNull(productDTO);
        assertEquals(productId, productDTO.getId());
        assertEquals(productEntity.getName(), productDTO.getName());
        assertEquals(productEntity.getDescription(), productDTO.getDescription());
        assertEquals(0, productDTO.getPrice().compareTo(productPrice));
        assertEquals("1000.00", productDTO.getPrice().toString());
        assertEquals(3, productDTO.getDiscounts().size());
        assertTrue(productDTO.getDiscounts().stream().anyMatch(discount -> discount.getPercentageRate() == 10));
        assertTrue(productDTO.getDiscounts().stream().anyMatch(discount -> discount.getPercentageRate() == 15));
        assertTrue(productDTO.getDiscounts().stream().anyMatch(discount -> discount.getPercentageRate() == 20));
        verify(productMapper).entityToDTO(eq(productEntity));
        assertTrue(memoryAppender.contains(PRODUCT_FOUND_MSG.formatted(productId), Level.INFO));

    }

    /**
     * Test purpose         - Verify if {@link ProductService#getProductById(UUID)}
     *                        returns {@link ProductDTO} with price containing higher fraction digits when
     *                        an associated currency is of higher fraction digits.
     * Test data            - product id, product price and currency fraction digits.
     * Test expected result - An instance of {@link ProductDTO} with proper data is returned.
     * Test type            - Positive.
     */
    @Test
    void getProductById_shouldReturnProductWithHigherFractionDigits_whenProductIsFoundAndAssociatedCurrencyContainsHigherFractionDigits() throws Exception {
        // Given
        UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        BigDecimal productPrice = new BigDecimal("1000.00");
        int currencyFractionDigits = 3;
        CurrencyEntity currencyEntity = buildCurrencyEntity("XXX", currencyFractionDigits);
        PercentageBasedDiscountEntity percentageBasedDiscountEntity = buildPercentageBasedDiscountEntity(10);
        Set<QuantityBasedDiscountEntity> quantityBasedDiscountEntities = Set.of(
                buildQuantityBasedDiscountEntity(15, 3, 5),
                buildQuantityBasedDiscountEntity(20, 6, null)
        );
        ProductEntity productEntity = buildProductEntity(productId, productPrice, currencyEntity,
                percentageBasedDiscountEntity, quantityBasedDiscountEntities);
        when(productRepository.findById(eq(productId))).thenReturn(Optional.of(productEntity));

        // When
        ProductDTO productDTO = productService.getProductById(productId);

        // Then
        assertNotNull(productDTO);
        assertEquals(productId, productDTO.getId());
        assertEquals(productEntity.getName(), productDTO.getName());
        assertEquals(productEntity.getDescription(), productDTO.getDescription());
        assertEquals(0, productDTO.getPrice().compareTo(productPrice));
        assertEquals("1000.000", productDTO.getPrice().toString());
        assertEquals(3, productDTO.getDiscounts().size());
        assertTrue(productDTO.getDiscounts().stream().anyMatch(discount -> discount.getPercentageRate() == 10));
        assertTrue(productDTO.getDiscounts().stream().anyMatch(discount -> discount.getPercentageRate() == 15));
        assertTrue(productDTO.getDiscounts().stream().anyMatch(discount -> discount.getPercentageRate() == 20));
        verify(productMapper).entityToDTO(eq(productEntity));
        assertTrue(memoryAppender.contains(PRODUCT_FOUND_MSG.formatted(productId), Level.INFO));
    }

    /**
     * Test purpose         - Verify if {@link ProductService#getProductById(UUID)}
     *                        throws {@link ProductNotFoundException} when product is not found.
     * Test data            - product id, product price and currency fraction digits.
     * Test expected result - {@link ProductNotFoundException} is thrown.
     * Test type            - Negative.
     */
    @Test
    void getProductById_shouldThrowProductNotFoundException_whenProductIsNotFound() {
        // Given
        UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        when(productRepository.findById(eq(productId))).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(productId));
        assertTrue(memoryAppender.contains(PRODUCT_NOT_FOUND_MSG.formatted(productId), Level.ERROR));
    }

    /**
     * Test purpose         - Verify if {@link ProductService#calculateProductPrice(UUID, int)}
     *                        returns {@link ProductPriceDTO} with proper data.
     * Test data            - product id, product price and product quantity.
     * Test expected result - An instance of {@link ProductPriceDTO} with proper data is returned.
     * Test type            - Positive.
     */
    @Test
    void calculatePrice_shouldCalculatePrice_whenProductIsFound() throws Exception {
        // Given
        UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        BigDecimal productPrice = new BigDecimal("1000.00");
        int productQuantity = 3;
        CurrencyEntity currencyEntity = buildCurrencyEntity("EUR", 2);
        PercentageBasedDiscountEntity percentageBasedDiscountEntity = buildPercentageBasedDiscountEntity(10);
        QuantityBasedDiscountEntity quantityBasedDiscountEntityToBeApplied = buildQuantityBasedDiscountEntity(15, 3, 5);
        Set<QuantityBasedDiscountEntity> quantityBasedDiscountEntities = Set.of(
                quantityBasedDiscountEntityToBeApplied,
                buildQuantityBasedDiscountEntity(20, 6, null)
        );
        ProductEntity productEntity = buildProductEntity(productId, productPrice, currencyEntity,
                percentageBasedDiscountEntity, quantityBasedDiscountEntities);
        when(productRepository.findById(eq(productId))).thenReturn(Optional.of(productEntity));

        // When
        ProductPriceDTO productPriceDTO = productService.calculateProductPrice(productId, productQuantity);

        // Then
        assertNotNull(productPriceDTO);
        assertEquals(productId, productPriceDTO.getProductId());
        assertEquals(0, productPriceDTO.getBaseItemPrice().compareTo(productPrice));
        assertTrue(productPriceDTO.getAppliedDiscounts().stream().anyMatch(discount -> discount.getPercentageRate() == 10));
        assertTrue(productPriceDTO.getAppliedDiscounts().stream().anyMatch(discount -> discount.getPercentageRate() == 15));
        assertTrue(productPriceDTO.getAppliedDiscounts().stream().noneMatch(discount -> discount.getPercentageRate() == 20));
        assertEquals("750.00", productPriceDTO.getItemPrice().toString());
        assertEquals("2250.00", productPriceDTO.getTotalPrice().toString());
        verify(productMapper).entityToPriceDTO(eq(productEntity), eq(percentageBasedDiscountEntity),
                eq(quantityBasedDiscountEntityToBeApplied), eq(productQuantity), any());
        assertTrue(memoryAppender.contains(PRODUCT_FOUND_MSG.formatted(productId), Level.INFO));
        assertTrue(memoryAppender.contains(MATCHING_PERCENTAGE_BASED_DISCOUNT_MSG.formatted(productId, 10), Level.INFO));
        assertTrue(memoryAppender.contains(MATCHING_QUANTITY_BASED_DISCOUNT_MSG.formatted(productId, 15), Level.INFO));
        assertTrue(memoryAppender.contains(PRODUCT_PRICE_CALCULATED_MSG.formatted(productId, productQuantity, "750.00"), Level.INFO));
    }

    /**
     * Test purpose         - Verify if {@link ProductService#calculateProductPrice(UUID, int)}
     *                        returns {@link ProductPriceDTO} with proper data and quantity based discount with
     *                        higher percentage rate applied.
     * Test data            - product id, product price and product quantity.
     * Test expected result - An instance of {@link ProductPriceDTO} with proper data is returned.
     * Test type            - Positive.
     */
    @Test
    void calculatePrice_shouldCalculatePriceAndApplyHigherQuantityBasedDiscount_whenProductIsFoundAndProductQuantityHasAppropriateValue() throws Exception {
        // Given
        UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        BigDecimal productPrice = new BigDecimal("1000.00");
        int productQuantity = 7;
        CurrencyEntity currencyEntity = buildCurrencyEntity("EUR", 2);
        PercentageBasedDiscountEntity percentageBasedDiscountEntity = buildPercentageBasedDiscountEntity(10);
        QuantityBasedDiscountEntity quantityBasedDiscountEntityToBeApplied = buildQuantityBasedDiscountEntity(20, 6, null);
        Set<QuantityBasedDiscountEntity> quantityBasedDiscountEntities = Set.of(
                quantityBasedDiscountEntityToBeApplied,
                buildQuantityBasedDiscountEntity(15, 3, 5)
        );
        ProductEntity productEntity = buildProductEntity(productId, productPrice, currencyEntity,
                percentageBasedDiscountEntity, quantityBasedDiscountEntities);
        when(productRepository.findById(eq(productId))).thenReturn(Optional.of(productEntity));

        // When
        ProductPriceDTO productPriceDTO = productService.calculateProductPrice(productId, productQuantity);

        // Then
        assertNotNull(productPriceDTO);
        assertEquals(productId, productPriceDTO.getProductId());
        assertEquals(0, productPriceDTO.getBaseItemPrice().compareTo(productPrice));
        assertTrue(productPriceDTO.getAppliedDiscounts().stream().anyMatch(discount -> discount.getPercentageRate() == 10));
        assertTrue(productPriceDTO.getAppliedDiscounts().stream().noneMatch(discount -> discount.getPercentageRate() == 15));
        assertTrue(productPriceDTO.getAppliedDiscounts().stream().anyMatch(discount -> discount.getPercentageRate() == 20));
        assertEquals("700.00", productPriceDTO.getItemPrice().toString());
        assertEquals("4900.00", productPriceDTO.getTotalPrice().toString());
        verify(productMapper).entityToPriceDTO(eq(productEntity), eq(percentageBasedDiscountEntity),
                eq(quantityBasedDiscountEntityToBeApplied), eq(productQuantity), any());
        assertTrue(memoryAppender.contains(PRODUCT_FOUND_MSG.formatted(productId), Level.INFO));
        assertTrue(memoryAppender.contains(MATCHING_PERCENTAGE_BASED_DISCOUNT_MSG.formatted(productId, 10), Level.INFO));
        assertTrue(memoryAppender.contains(MATCHING_QUANTITY_BASED_DISCOUNT_MSG.formatted(productId, 20), Level.INFO));
        assertTrue(memoryAppender.contains(PRODUCT_PRICE_CALCULATED_MSG.formatted(productId, productQuantity, "700.00"), Level.INFO));
    }

    /**
     * Test purpose         - Verify if {@link ProductService#calculateProductPrice(UUID, int)}
     *                        returns {@link ProductPriceDTO} with price containing higher fraction digits when
     *                        an associated currency is of higher fraction digits.
     * Test data            - product id, product price and currency fraction digits.
     * Test expected result - An instance of {@link ProductPriceDTO} with proper data is returned.
     * Test type            - Positive.
     */
    @Test
    void calculatePrice_shouldCalculatePriceWithHigherFractionDigits_whenProductIsFoundAndAssociatedCurrencyContainsHigherFractionDigits() throws Exception {
        // Given
        UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        BigDecimal productPrice = new BigDecimal("1000.00");
        int productQuantity = 3;
        int currencyFractionDigits = 3;
        CurrencyEntity currencyEntity = buildCurrencyEntity("XXX", currencyFractionDigits);
        PercentageBasedDiscountEntity percentageBasedDiscountEntity = buildPercentageBasedDiscountEntity(10);
        QuantityBasedDiscountEntity quantityBasedDiscountEntityToBeApplied = buildQuantityBasedDiscountEntity(15, 3, 5);
        Set<QuantityBasedDiscountEntity> quantityBasedDiscountEntities = Set.of(
                quantityBasedDiscountEntityToBeApplied,
                buildQuantityBasedDiscountEntity(20, 6, null)
        );
        ProductEntity productEntity = buildProductEntity(productId, productPrice, currencyEntity,
                percentageBasedDiscountEntity, quantityBasedDiscountEntities);
        when(productRepository.findById(eq(productId))).thenReturn(Optional.of(productEntity));

        // When
        ProductPriceDTO productPriceDTO = productService.calculateProductPrice(productId, productQuantity);

        // Then
        assertNotNull(productPriceDTO);
        assertEquals(productId, productPriceDTO.getProductId());
        assertEquals(0, productPriceDTO.getBaseItemPrice().compareTo(productPrice));
        assertTrue(productPriceDTO.getAppliedDiscounts().stream().anyMatch(discount -> discount.getPercentageRate() == 10));
        assertTrue(productPriceDTO.getAppliedDiscounts().stream().anyMatch(discount -> discount.getPercentageRate() == 15));
        assertTrue(productPriceDTO.getAppliedDiscounts().stream().noneMatch(discount -> discount.getPercentageRate() == 20));
        assertEquals("750.000", productPriceDTO.getItemPrice().toString());
        assertEquals("2250.000", productPriceDTO.getTotalPrice().toString());
        verify(productMapper).entityToPriceDTO(eq(productEntity), eq(percentageBasedDiscountEntity),
                eq(quantityBasedDiscountEntityToBeApplied), eq(productQuantity), any());
        assertTrue(memoryAppender.contains(PRODUCT_FOUND_MSG.formatted(productId), Level.INFO));
        assertTrue(memoryAppender.contains(MATCHING_PERCENTAGE_BASED_DISCOUNT_MSG.formatted(productId, 10), Level.INFO));
        assertTrue(memoryAppender.contains(MATCHING_QUANTITY_BASED_DISCOUNT_MSG.formatted(productId, 15), Level.INFO));
        assertTrue(memoryAppender.contains(PRODUCT_PRICE_CALCULATED_MSG.formatted(productId, productQuantity, "750.000"), Level.INFO));
    }

    @Test
    void calculatePrice_shouldCalculatePriceAndApplyHigherQuantityBasedDiscount_whenProductIsFoundAndQuantityBasedDiscountsThresholdsOverlap() throws Exception {
        // Given
        UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        BigDecimal productPrice = new BigDecimal("1000.00");
        int productQuantity = 7;
        CurrencyEntity currencyEntity = buildCurrencyEntity("EUR", 2);
        PercentageBasedDiscountEntity percentageBasedDiscountEntity = buildPercentageBasedDiscountEntity(10);
        QuantityBasedDiscountEntity quantityBasedDiscountEntityToBeApplied = buildQuantityBasedDiscountEntity(20, 6, null);
        Set<QuantityBasedDiscountEntity> quantityBasedDiscountEntities = Set.of(
                quantityBasedDiscountEntityToBeApplied,
                buildQuantityBasedDiscountEntity(15, 6, 13)
        );
        ProductEntity productEntity = buildProductEntity(productId, productPrice, currencyEntity,
                percentageBasedDiscountEntity, quantityBasedDiscountEntities);
        when(productRepository.findById(eq(productId))).thenReturn(Optional.of(productEntity));

        // When
        ProductPriceDTO productPriceDTO = productService.calculateProductPrice(productId, productQuantity);

        // Then
        assertNotNull(productPriceDTO);
        assertEquals(productId, productPriceDTO.getProductId());
        assertEquals(0, productPriceDTO.getBaseItemPrice().compareTo(productPrice));
        assertTrue(productPriceDTO.getAppliedDiscounts().stream().anyMatch(discount -> discount.getPercentageRate() == 10));
        assertTrue(productPriceDTO.getAppliedDiscounts().stream().noneMatch(discount -> discount.getPercentageRate() == 15));
        assertTrue(productPriceDTO.getAppliedDiscounts().stream().anyMatch(discount -> discount.getPercentageRate() == 20));
        assertEquals("700.00", productPriceDTO.getItemPrice().toString());
        assertEquals("4900.00", productPriceDTO.getTotalPrice().toString());
        verify(productMapper).entityToPriceDTO(eq(productEntity), eq(percentageBasedDiscountEntity),
                eq(quantityBasedDiscountEntityToBeApplied), eq(productQuantity), any());
        assertTrue(memoryAppender.contains(PRODUCT_FOUND_MSG.formatted(productId), Level.INFO));
        assertTrue(memoryAppender.contains(MATCHING_PERCENTAGE_BASED_DISCOUNT_MSG.formatted(productId, 10), Level.INFO));
        assertTrue(memoryAppender.contains(MATCHING_QUANTITY_BASED_DISCOUNT_MSG.formatted(productId, 20), Level.INFO));
        assertTrue(memoryAppender.contains(PRODUCT_PRICE_CALCULATED_MSG.formatted(productId, productQuantity, "700.00"), Level.INFO));
    }

    @Test
    void calculatePrice_shouldCalculatePriceEqualToZero_whenProductIsFoundAndDiscountRatesSumIsMoreThan100Percent() throws Exception {
        // Given
        UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        BigDecimal productPrice = new BigDecimal("1000.00");
        int productQuantity = 3;
        CurrencyEntity currencyEntity = buildCurrencyEntity("EUR", 2);
        PercentageBasedDiscountEntity percentageBasedDiscountEntity = buildPercentageBasedDiscountEntity(90);
        QuantityBasedDiscountEntity quantityBasedDiscountEntityToBeApplied = buildQuantityBasedDiscountEntity(15, 3, 5);
        Set<QuantityBasedDiscountEntity> quantityBasedDiscountEntities = Set.of(
                quantityBasedDiscountEntityToBeApplied,
                buildQuantityBasedDiscountEntity(20, 6, null)
        );
        ProductEntity productEntity = buildProductEntity(productId, productPrice, currencyEntity,
                percentageBasedDiscountEntity, quantityBasedDiscountEntities);
        when(productRepository.findById(eq(productId))).thenReturn(Optional.of(productEntity));

        // When
        ProductPriceDTO productPriceDTO = productService.calculateProductPrice(productId, productQuantity);
        assertTrue(memoryAppender.contains(PRODUCT_FOUND_MSG.formatted(productId), Level.INFO));
        assertTrue(memoryAppender.contains(MATCHING_PERCENTAGE_BASED_DISCOUNT_MSG.formatted(productId, 90), Level.INFO));
        assertTrue(memoryAppender.contains(MATCHING_QUANTITY_BASED_DISCOUNT_MSG.formatted(productId, 15), Level.INFO));
        assertTrue(memoryAppender.contains(DISCOUNTS_SUM_MORE_THAN_100_PERCENT_MSG, Level.INFO));
        assertTrue(memoryAppender.contains(PRODUCT_PRICE_CALCULATED_MSG.formatted(productId, productQuantity, "0.00"), Level.INFO));

        // Then
        assertNotNull(productPriceDTO);
        assertEquals(productId, productPriceDTO.getProductId());
        assertEquals(0, productPriceDTO.getBaseItemPrice().compareTo(productPrice));
        assertTrue(productPriceDTO.getAppliedDiscounts().stream().anyMatch(discount -> discount.getPercentageRate() == 90));
        assertTrue(productPriceDTO.getAppliedDiscounts().stream().anyMatch(discount -> discount.getPercentageRate() == 15));
        assertTrue(productPriceDTO.getAppliedDiscounts().stream().noneMatch(discount -> discount.getPercentageRate() == 20));
        assertEquals("0.00", productPriceDTO.getItemPrice().toString());
        assertEquals("0.00", productPriceDTO.getTotalPrice().toString());
        verify(productMapper).entityToPriceDTO(eq(productEntity), eq(percentageBasedDiscountEntity),
                eq(quantityBasedDiscountEntityToBeApplied), eq(productQuantity), any());
    }

    /**
     * Test purpose         - Verify if {@link ProductService#calculateProductPrice(UUID, int)}
     *                        throws {@link ProductNotFoundException} when product is not found.
     * Test data            - product id and product quantity.
     * Test expected result - {@link ProductNotFoundException} is thrown.
     * Test type            - Negative.
     */
    @Test
    void calculatePrice_shouldThrowProductNotFoundException_whenProductIsNotFound() {
        // Given
        UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        int productQuantity = 3;
        when(productRepository.findById(eq(productId))).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ProductNotFoundException.class, () -> productService.calculateProductPrice(productId, productQuantity));
        assertTrue(memoryAppender.contains(PRODUCT_NOT_FOUND_MSG.formatted(productId), Level.ERROR));
    }

    /**
     * Test purpose         - Verify if {@link ProductService#calculateProductPrice(UUID, int)}
     *                        throws {@link IllegalArgumentException} when product quantity is less than 1.
     * Test data            - product id and product quantity.
     * Test expected result - {@link IllegalArgumentException} is thrown.
     * Test type            - Negative.
     */
    @Test
    void calculatePrice_shouldThrowIllegalArgumentException_whenProductQuantityIsLessThanOne() {
        // Given
        UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        int productQuantity = 0;

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> productService.calculateProductPrice(productId, productQuantity));
        assertTrue(memoryAppender.contains(PRODUCT_QUANTITY_ERROR_MSG, Level.ERROR));
    }

}