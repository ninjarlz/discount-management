package pl.tul.discountmanagement.unit.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import pl.tul.discountmanagement.product.application.dto.ProductDTO;
import pl.tul.discountmanagement.product.application.dto.ProductPriceDTO;
import pl.tul.discountmanagement.product.application.mapper.ProductDTOMapper;
import pl.tul.discountmanagement.product.application.port.out.persistence.ProductPersistencePort;
import pl.tul.discountmanagement.product.application.service.ProductApplicationService;
import pl.tul.discountmanagement.product.domain.exception.ProductNotFoundException;
import pl.tul.discountmanagement.product.domain.model.Currency;
import pl.tul.discountmanagement.product.domain.model.PercentageBasedDiscount;
import pl.tul.discountmanagement.product.domain.model.Product;
import pl.tul.discountmanagement.product.domain.model.QuantityBasedDiscount;
import pl.tul.discountmanagement.unit.logging.MemoryAppender;

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
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pl.tul.discountmanagement.unit.util.TestDataUtils.buildCurrency;
import static pl.tul.discountmanagement.unit.util.TestDataUtils.buildPercentageBasedDiscount;
import static pl.tul.discountmanagement.unit.util.TestDataUtils.buildProduct;
import static pl.tul.discountmanagement.unit.util.TestDataUtils.buildQuantityBasedDiscount;
import static pl.tul.discountmanagement.util.constant.TestConstants.DISCOUNTS_SUM_EQUALS_TO_OR_MORE_THAN_100_PERCENT_LOG_MSG;
import static pl.tul.discountmanagement.util.constant.TestConstants.MATCHING_PERCENTAGE_BASED_DISCOUNT_LOG_MSG;
import static pl.tul.discountmanagement.util.constant.TestConstants.MATCHING_QUANTITY_BASED_DISCOUNT_LOG_MSG;
import static pl.tul.discountmanagement.util.constant.TestConstants.PRODUCT_FOUND_LOG_MSG;
import static pl.tul.discountmanagement.util.constant.TestConstants.PRODUCT_NOT_FOUND_LOG_MSG;
import static pl.tul.discountmanagement.util.constant.TestConstants.PRODUCT_PRICE_CALCULATED_LOG_MSG;
import static pl.tul.discountmanagement.util.constant.TestConstants.PRODUCT_QUANTITY_ERROR_LOG_MSG;

/**
 * Test class for {@link ProductApplicationService}.
 */
@ExtendWith(MockitoExtension.class)
class ProductApplicationServiceTest {

    private static MemoryAppender memoryAppender;

    @Mock
    private ProductPersistencePort productPersistencePort;

    @Spy
    private ProductDTOMapper productDTOMapper;

    @InjectMocks
    private ProductApplicationService productService;

    /**
     * Setup logger.
     */
    @BeforeAll
    static void setUpLogger() {
        Logger logger = (Logger) LoggerFactory.getLogger(ProductApplicationService.class);
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
     * Test purpose         - Verify if {@link ProductApplicationService#getProductById(UUID)}
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
        Currency currency = buildCurrency("EUR", 2);
        PercentageBasedDiscount percentageBasedDiscount = buildPercentageBasedDiscount(10);
        Set<QuantityBasedDiscount> quantityBasedDiscounts = Set.of(
          buildQuantityBasedDiscount(15, 3, 5),
          buildQuantityBasedDiscount(20, 6, null)
        );
        Product product = buildProduct(productId, productPrice, currency,
                percentageBasedDiscount, quantityBasedDiscounts);
        when(productPersistencePort.findById(eq(productId))).thenReturn(Optional.of(product));

        // When
        ProductDTO productDTO = productService.getProductById(productId);

        // Then
        assertNotNull(productDTO);
        assertEquals(productId, productDTO.getId());
        assertEquals(product.name(), productDTO.getName());
        assertEquals(product.description(), productDTO.getDescription());
        assertEquals(0, productDTO.getPrice().compareTo(productPrice));
        assertEquals("1000.00", productDTO.getPrice().toString());
        assertEquals(3, productDTO.getDiscounts().size());
        assertTrue(productDTO.getDiscounts().stream().anyMatch(discount -> discount.getPercentageRate() == 10));
        assertTrue(productDTO.getDiscounts().stream().anyMatch(discount -> discount.getPercentageRate() == 15));
        assertTrue(productDTO.getDiscounts().stream().anyMatch(discount -> discount.getPercentageRate() == 20));
        verify(productDTOMapper).domainToDTO(eq(product));
        assertTrue(memoryAppender.contains(PRODUCT_FOUND_LOG_MSG.formatted(productId), Level.INFO));
    }

    /**
     * Test purpose         - Verify if {@link ProductApplicationService#getProductById(UUID)}
     *                        returns {@link ProductDTO} with proper data and without percentage-based discount when
     *                        there is no associated percentage-based discount.
     * Test data            - product id and product price.
     * Test expected result - An instance of {@link ProductDTO} with proper data is returned.
     * Test type            - Positive.
     */
    @Test
    void getProductById_shouldReturnProductWithoutPercentageBasedDiscount_whenProductIsFoundWithNoAssociatedPercentageBasedDiscount() throws Exception {
        // Given
        UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        BigDecimal productPrice = new BigDecimal("1000.00");
        Currency currency = buildCurrency("EUR", 2);
        Set<QuantityBasedDiscount> quantityBasedDiscounts = Set.of(
                buildQuantityBasedDiscount(15, 3, 5),
                buildQuantityBasedDiscount(20, 6, null)
        );
        Product product = buildProduct(productId, productPrice, currency,
                null, quantityBasedDiscounts);
        when(productPersistencePort.findById(eq(productId))).thenReturn(Optional.of(product));

        // When
        ProductDTO productDTO = productService.getProductById(productId);

        // Then
        assertNotNull(productDTO);
        assertEquals(productId, productDTO.getId());
        assertEquals(product.name(), productDTO.getName());
        assertEquals(product.description(), productDTO.getDescription());
        assertEquals(0, productDTO.getPrice().compareTo(productPrice));
        assertEquals("1000.00", productDTO.getPrice().toString());
        assertEquals(2, productDTO.getDiscounts().size());
        assertTrue(productDTO.getDiscounts().stream().noneMatch(discount -> discount.getPercentageRate() == 10));
        assertTrue(productDTO.getDiscounts().stream().anyMatch(discount -> discount.getPercentageRate() == 15));
        assertTrue(productDTO.getDiscounts().stream().anyMatch(discount -> discount.getPercentageRate() == 20));
        verify(productDTOMapper).domainToDTO(eq(product));
        assertTrue(memoryAppender.contains(PRODUCT_FOUND_LOG_MSG.formatted(productId), Level.INFO));
    }

    /**
     * Test purpose         - Verify if {@link ProductApplicationService#getProductById(UUID)}
     *                        returns {@link ProductDTO} with proper data and without any quantity-based discount when
     *                        there are no associated quantity-based discounts.
     * Test data            - product id and product price.
     * Test expected result - An instance of {@link ProductDTO} with proper data is returned.
     * Test type            - Positive.
     */
    @Test
    void getProductById_shouldReturnProductWithoutQuantityBasedDiscounts_whenProductIsFoundWithNoAssociatedQuantityBasedDiscounts() throws Exception {
        // Given
        UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        BigDecimal productPrice = new BigDecimal("1000.00");
        Currency currency = buildCurrency("EUR", 2);
        PercentageBasedDiscount percentageBasedDiscount = buildPercentageBasedDiscount(10);
        Product product = buildProduct(productId, productPrice, currency,
                percentageBasedDiscount, null);
        when(productPersistencePort.findById(eq(productId))).thenReturn(Optional.of(product));

        // When
        ProductDTO productDTO = productService.getProductById(productId);

        // Then
        assertNotNull(productDTO);
        assertEquals(productId, productDTO.getId());
        assertEquals(product.name(), productDTO.getName());
        assertEquals(product.description(), productDTO.getDescription());
        assertEquals(0, productDTO.getPrice().compareTo(productPrice));
        assertEquals("1000.00", productDTO.getPrice().toString());
        assertEquals(1, productDTO.getDiscounts().size());
        assertTrue(productDTO.getDiscounts().stream().anyMatch(discount -> discount.getPercentageRate() == 10));
        assertTrue(productDTO.getDiscounts().stream().noneMatch(discount -> discount.getPercentageRate() == 15));
        assertTrue(productDTO.getDiscounts().stream().noneMatch(discount -> discount.getPercentageRate() == 20));
        verify(productDTOMapper).domainToDTO(eq(product));
        assertTrue(memoryAppender.contains(PRODUCT_FOUND_LOG_MSG.formatted(productId), Level.INFO));
    }

    /**
     * Test purpose         - Verify if {@link ProductApplicationService#getProductById(UUID)}
     *                        returns {@link ProductDTO} with proper data and without any discount when
     *                        there are no associated discounts.
     * Test data            - product id and product price.
     * Test expected result - An instance of {@link ProductDTO} with proper data is returned.
     * Test type            - Positive.
     */
    @Test
    void getProductById_shouldReturnProductWithoutAnyDiscount_whenProductIsFoundWithNoAssociatedDiscounts() throws Exception {
        // Given
        UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        BigDecimal productPrice = new BigDecimal("1000.00");
        Currency currency = buildCurrency("EUR", 2);
        Product product = buildProduct(productId, productPrice, currency,
                null, null);
        when(productPersistencePort.findById(eq(productId))).thenReturn(Optional.of(product));

        // When
        ProductDTO productDTO = productService.getProductById(productId);

        // Then
        assertNotNull(productDTO);
        assertEquals(productId, productDTO.getId());
        assertEquals(product.name(), productDTO.getName());
        assertEquals(product.description(), productDTO.getDescription());
        assertEquals(0, productDTO.getPrice().compareTo(productPrice));
        assertEquals("1000.00", productDTO.getPrice().toString());
        assertTrue(productDTO.getDiscounts().isEmpty());
        verify(productDTOMapper).domainToDTO(eq(product));
        assertTrue(memoryAppender.contains(PRODUCT_FOUND_LOG_MSG.formatted(productId), Level.INFO));
    }

    /**
     * Test purpose         - Verify if {@link ProductApplicationService#getProductById(UUID)}
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
        Currency currency = buildCurrency("XXX", currencyFractionDigits);
        PercentageBasedDiscount percentageBasedDiscount = buildPercentageBasedDiscount(10);
        Set<QuantityBasedDiscount> quantityBasedDiscounts = Set.of(
                buildQuantityBasedDiscount(15, 3, 5),
                buildQuantityBasedDiscount(20, 6, null)
        );
        Product product = buildProduct(productId, productPrice, currency,
                percentageBasedDiscount, quantityBasedDiscounts);
        when(productPersistencePort.findById(eq(productId))).thenReturn(Optional.of(product));

        // When
        ProductDTO productDTO = productService.getProductById(productId);

        // Then
        assertNotNull(productDTO);
        assertEquals(productId, productDTO.getId());
        assertEquals(product.name(), productDTO.getName());
        assertEquals(product.description(), productDTO.getDescription());
        assertEquals(0, productDTO.getPrice().compareTo(productPrice));
        assertEquals("1000.000", productDTO.getPrice().toString());
        assertEquals(3, productDTO.getDiscounts().size());
        assertTrue(productDTO.getDiscounts().stream().anyMatch(discount -> discount.getPercentageRate() == 10));
        assertTrue(productDTO.getDiscounts().stream().anyMatch(discount -> discount.getPercentageRate() == 15));
        assertTrue(productDTO.getDiscounts().stream().anyMatch(discount -> discount.getPercentageRate() == 20));
        verify(productDTOMapper).domainToDTO(eq(product));
        assertTrue(memoryAppender.contains(PRODUCT_FOUND_LOG_MSG.formatted(productId), Level.INFO));
    }

    /**
     * Test purpose         - Verify if {@link ProductApplicationService#getProductById(UUID)}
     *                        throws {@link ProductNotFoundException} when product is not found.
     * Test data            - product id, product price and currency fraction digits.
     * Test expected result - {@link ProductNotFoundException} is thrown.
     * Test type            - Negative.
     */
    @Test
    void getProductById_shouldThrowProductNotFoundException_whenProductIsNotFound() {
        // Given
        UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        when(productPersistencePort.findById(eq(productId))).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(productId));
        assertTrue(memoryAppender.contains(PRODUCT_NOT_FOUND_LOG_MSG.formatted(productId), Level.ERROR));
    }

    /**
     * Test purpose         - Verify if {@link ProductApplicationService#calculateProductPrice(UUID, int)}
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
        Currency currency = buildCurrency("EUR", 2);
        PercentageBasedDiscount percentageBasedDiscount = buildPercentageBasedDiscount(10);
        QuantityBasedDiscount quantityBasedDiscountToBeApplied = buildQuantityBasedDiscount(15, 3, 5);
        Set<QuantityBasedDiscount> quantityBasedDiscounts = Set.of(
                quantityBasedDiscountToBeApplied,
                buildQuantityBasedDiscount(20, 6, null)
        );
        Product product = buildProduct(productId, productPrice, currency,
                percentageBasedDiscount, quantityBasedDiscounts);
        when(productPersistencePort.findById(eq(productId))).thenReturn(Optional.of(product));

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
        verify(productDTOMapper).toPriceDTO(eq(product), eq(percentageBasedDiscount),
                eq(quantityBasedDiscountToBeApplied), eq(productQuantity), any(), any());
        assertTrue(memoryAppender.contains(PRODUCT_FOUND_LOG_MSG.formatted(productId), Level.INFO));
        assertTrue(memoryAppender.contains(MATCHING_PERCENTAGE_BASED_DISCOUNT_LOG_MSG.formatted(productId, 10), Level.INFO));
        assertTrue(memoryAppender.contains(MATCHING_QUANTITY_BASED_DISCOUNT_LOG_MSG.formatted(productId, 15), Level.INFO));
        assertTrue(memoryAppender.contains(PRODUCT_PRICE_CALCULATED_LOG_MSG.formatted(productId, productQuantity, "2250.00 EUR", "750.00 EUR"), Level.INFO));
    }

    /**
     * Test purpose         - Verify if {@link ProductApplicationService#calculateProductPrice(UUID, int)}
     *                        returns {@link ProductPriceDTO} with proper data and only percentage-based discount applied
     *                        when product quantity is not high enough.
     * Test data            - product id, product price and product quantity.
     * Test expected result - An instance of {@link ProductPriceDTO} with proper data is returned.
     * Test type            - Positive.
     */
    @Test
    void calculatePrice_shouldCalculatePriceAndApplyOnlyPercentageBasedDiscount_whenProductIsFoundAndProductQuantityIsNotHighEnough() throws Exception {
        // Given
        UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        BigDecimal productPrice = new BigDecimal("1000.00");
        int productQuantity = 1;
        Currency currency = buildCurrency("EUR", 2);
        PercentageBasedDiscount percentageBasedDiscount = buildPercentageBasedDiscount(10);
        Set<QuantityBasedDiscount> quantityBasedDiscounts = Set.of(
                buildQuantityBasedDiscount(15, 3, 5),
                buildQuantityBasedDiscount(20, 6, null)
        );
        Product product = buildProduct(productId, productPrice, currency,
                percentageBasedDiscount, quantityBasedDiscounts);
        when(productPersistencePort.findById(eq(productId))).thenReturn(Optional.of(product));

        // When
        ProductPriceDTO productPriceDTO = productService.calculateProductPrice(productId, productQuantity);

        // Then
        assertNotNull(productPriceDTO);
        assertEquals(productId, productPriceDTO.getProductId());
        assertEquals(0, productPriceDTO.getBaseItemPrice().compareTo(productPrice));
        assertTrue(productPriceDTO.getAppliedDiscounts().stream().anyMatch(discount -> discount.getPercentageRate() == 10));
        assertTrue(productPriceDTO.getAppliedDiscounts().stream().noneMatch(discount -> discount.getPercentageRate() == 15));
        assertTrue(productPriceDTO.getAppliedDiscounts().stream().noneMatch(discount -> discount.getPercentageRate() == 20));
        assertEquals("900.00", productPriceDTO.getItemPrice().toString());
        assertEquals("900.00", productPriceDTO.getTotalPrice().toString());
        verify(productDTOMapper).toPriceDTO(eq(product), eq(percentageBasedDiscount), isNull(), eq(productQuantity), any(), any());
        assertTrue(memoryAppender.contains(PRODUCT_FOUND_LOG_MSG.formatted(productId), Level.INFO));
        assertTrue(memoryAppender.contains(MATCHING_PERCENTAGE_BASED_DISCOUNT_LOG_MSG.formatted(productId, 10), Level.INFO));
        assertTrue(memoryAppender.contains(PRODUCT_PRICE_CALCULATED_LOG_MSG.formatted(productId, productQuantity, "900.00 EUR" ,"900.00 EUR"), Level.INFO));
    }

    /**
     * Test purpose         - Verify if {@link ProductApplicationService#calculateProductPrice(UUID, int)}
     *                        returns {@link ProductPriceDTO} with proper data and only quantity-based discount applied
     *                        when there is no associated percentage-based discount.
     * Test data            - product id, product price and product quantity.
     * Test expected result - An instance of {@link ProductPriceDTO} with proper data is returned.
     * Test type            - Positive.
     */
    @Test
    void calculatePrice_shouldCalculatePriceAndApplyOnlyQuantityBasedDiscount_whenProductIsFoundAndThereIsNoAssociatedPercentageBasedDiscount() throws Exception {
        // Given
        UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        BigDecimal productPrice = new BigDecimal("1000.00");
        int productQuantity = 3;
        Currency currency = buildCurrency("EUR", 2);
        QuantityBasedDiscount quantityBasedDiscountToBeApplied = buildQuantityBasedDiscount(15, 3, 5);
        Set<QuantityBasedDiscount> quantityBasedDiscounts = Set.of(
                quantityBasedDiscountToBeApplied,
                buildQuantityBasedDiscount(20, 6, null)
        );
        Product product = buildProduct(productId, productPrice, currency, null, quantityBasedDiscounts);
        when(productPersistencePort.findById(eq(productId))).thenReturn(Optional.of(product));

        // When
        ProductPriceDTO productPriceDTO = productService.calculateProductPrice(productId, productQuantity);

        // Then
        assertNotNull(productPriceDTO);
        assertEquals(productId, productPriceDTO.getProductId());
        assertEquals(0, productPriceDTO.getBaseItemPrice().compareTo(productPrice));
        assertTrue(productPriceDTO.getAppliedDiscounts().stream().noneMatch(discount -> discount.getPercentageRate() == 10));
        assertTrue(productPriceDTO.getAppliedDiscounts().stream().anyMatch(discount -> discount.getPercentageRate() == 15));
        assertTrue(productPriceDTO.getAppliedDiscounts().stream().noneMatch(discount -> discount.getPercentageRate() == 20));
        assertEquals("850.00", productPriceDTO.getItemPrice().toString());
        assertEquals("2550.00", productPriceDTO.getTotalPrice().toString());
        verify(productDTOMapper).toPriceDTO(eq(product), isNull(), eq(quantityBasedDiscountToBeApplied), eq(productQuantity), any(), any());
        assertTrue(memoryAppender.contains(PRODUCT_FOUND_LOG_MSG.formatted(productId), Level.INFO));
        assertTrue(memoryAppender.contains(MATCHING_QUANTITY_BASED_DISCOUNT_LOG_MSG.formatted(productId, 15), Level.INFO));
        assertTrue(memoryAppender.contains(PRODUCT_PRICE_CALCULATED_LOG_MSG.formatted(productId, productQuantity, "2550.00 EUR", "850.00 EUR"), Level.INFO));
    }

    /**
     * Test purpose         - Verify if {@link ProductApplicationService#calculateProductPrice(UUID, int)}
     *                        returns {@link ProductPriceDTO} with proper data and not any discount applied when there
     *                        is no associated percentage-based discount and product quantity is not high enough.
     * Test data            - product id, product price and product quantity.
     * Test expected result - An instance of {@link ProductPriceDTO} with proper data is returned.
     * Test type            - Positive.
     */
    @Test
    void calculatePrice_shouldCalculatePriceAndNotApplyAnyDiscount_whenProductIsFoundAndThereIsNoAssociatedPercentageBasedDiscountAndProductQuantityIsNotHighEnough() throws Exception {
        // Given
        UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        BigDecimal productPrice = new BigDecimal("1000.00");
        int productQuantity = 1;
        Currency currency = buildCurrency("EUR", 2);
        Set<QuantityBasedDiscount> quantityBasedDiscounts = Set.of(
                buildQuantityBasedDiscount(15, 3, 5),
                buildQuantityBasedDiscount(20, 6, null)
        );
        Product product = buildProduct(productId, productPrice, currency, null, quantityBasedDiscounts);
        when(productPersistencePort.findById(eq(productId))).thenReturn(Optional.of(product));

        // When
        ProductPriceDTO productPriceDTO = productService.calculateProductPrice(productId, productQuantity);

        // Then
        assertNotNull(productPriceDTO);
        assertEquals(productId, productPriceDTO.getProductId());
        assertEquals(0, productPriceDTO.getBaseItemPrice().compareTo(productPrice));
        assertTrue(productPriceDTO.getAppliedDiscounts().stream().noneMatch(discount -> discount.getPercentageRate() == 10));
        assertTrue(productPriceDTO.getAppliedDiscounts().stream().noneMatch(discount -> discount.getPercentageRate() == 15));
        assertTrue(productPriceDTO.getAppliedDiscounts().stream().noneMatch(discount -> discount.getPercentageRate() == 20));
        assertEquals("1000.00", productPriceDTO.getItemPrice().toString());
        assertEquals("1000.00", productPriceDTO.getTotalPrice().toString());
        verify(productDTOMapper).toPriceDTO(eq(product), isNull(), isNull(), eq(productQuantity), any(), any());
        assertTrue(memoryAppender.contains(PRODUCT_FOUND_LOG_MSG.formatted(productId), Level.INFO));
        assertTrue(memoryAppender.contains(PRODUCT_PRICE_CALCULATED_LOG_MSG.formatted(productId, productQuantity, "1000.00 EUR", "1000.00 EUR"), Level.INFO));
    }

    /**
     * Test purpose         - Verify if {@link ProductApplicationService#calculateProductPrice(UUID, int)}
     *                        returns {@link ProductPriceDTO} with proper data and not any discount applied when there
     *                        is no associated discount.
     * Test data            - product id, product price and product quantity.
     * Test expected result - An instance of {@link ProductPriceDTO} with proper data is returned.
     * Test type            - Positive.
     */
    @Test
    void calculatePrice_shouldCalculatePriceAndNotApplyAnyDiscount_whenProductIsFoundAndThereIsNoAssociatedDiscount() throws Exception {
        // Given
        UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        BigDecimal productPrice = new BigDecimal("1000.00");
        int productQuantity = 1;
        Currency currency = buildCurrency("EUR", 2);
        Product product = buildProduct(productId, productPrice, currency, null, null);
        when(productPersistencePort.findById(eq(productId))).thenReturn(Optional.of(product));

        // When
        ProductPriceDTO productPriceDTO = productService.calculateProductPrice(productId, productQuantity);

        // Then
        assertNotNull(productPriceDTO);
        assertEquals(productId, productPriceDTO.getProductId());
        assertEquals(0, productPriceDTO.getBaseItemPrice().compareTo(productPrice));
        assertTrue(productPriceDTO.getAppliedDiscounts().stream().noneMatch(discount -> discount.getPercentageRate() == 10));
        assertTrue(productPriceDTO.getAppliedDiscounts().stream().noneMatch(discount -> discount.getPercentageRate() == 15));
        assertTrue(productPriceDTO.getAppliedDiscounts().stream().noneMatch(discount -> discount.getPercentageRate() == 20));
        assertEquals("1000.00", productPriceDTO.getItemPrice().toString());
        assertEquals("1000.00", productPriceDTO.getTotalPrice().toString());
        verify(productDTOMapper).toPriceDTO(eq(product), isNull(), isNull(), eq(productQuantity), any(), any());
        assertTrue(memoryAppender.contains(PRODUCT_FOUND_LOG_MSG.formatted(productId), Level.INFO));
        assertTrue(memoryAppender.contains(PRODUCT_PRICE_CALCULATED_LOG_MSG.formatted(productId, productQuantity, "1000.00 EUR", "1000.00 EUR"), Level.INFO));
    }

    /**
     * Test purpose         - Verify if {@link ProductApplicationService#calculateProductPrice(UUID, int)}
     *                        returns {@link ProductPriceDTO} with proper data and quantity-based discount with
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
        Currency currency = buildCurrency("EUR", 2);
        PercentageBasedDiscount percentageBasedDiscount = buildPercentageBasedDiscount(10);
        QuantityBasedDiscount quantityBasedDiscountToBeApplied = buildQuantityBasedDiscount(20, 6, null);
        Set<QuantityBasedDiscount> quantityBasedDiscounts = Set.of(
                quantityBasedDiscountToBeApplied,
                buildQuantityBasedDiscount(15, 3, 5)
        );
        Product product = buildProduct(productId, productPrice, currency,
                percentageBasedDiscount, quantityBasedDiscounts);
        when(productPersistencePort.findById(eq(productId))).thenReturn(Optional.of(product));

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
        verify(productDTOMapper).toPriceDTO(eq(product), eq(percentageBasedDiscount),
                eq(quantityBasedDiscountToBeApplied), eq(productQuantity), any(), any());
        assertTrue(memoryAppender.contains(PRODUCT_FOUND_LOG_MSG.formatted(productId), Level.INFO));
        assertTrue(memoryAppender.contains(MATCHING_PERCENTAGE_BASED_DISCOUNT_LOG_MSG.formatted(productId, 10), Level.INFO));
        assertTrue(memoryAppender.contains(MATCHING_QUANTITY_BASED_DISCOUNT_LOG_MSG.formatted(productId, 20), Level.INFO));
        assertTrue(memoryAppender.contains(PRODUCT_PRICE_CALCULATED_LOG_MSG.formatted(productId, productQuantity, "4900.00 EUR", "700.00 EUR"), Level.INFO));
    }

    /**
     * Test purpose         - Verify if {@link ProductApplicationService#calculateProductPrice(UUID, int)}
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
        Currency currency = buildCurrency("XXX", currencyFractionDigits);
        PercentageBasedDiscount percentageBasedDiscount = buildPercentageBasedDiscount(10);
        QuantityBasedDiscount quantityBasedDiscountToBeApplied = buildQuantityBasedDiscount(15, 3, 5);
        Set<QuantityBasedDiscount> quantityBasedDiscounts = Set.of(
                quantityBasedDiscountToBeApplied,
                buildQuantityBasedDiscount(20, 6, null)
        );
        Product product = buildProduct(productId, productPrice, currency,
                percentageBasedDiscount, quantityBasedDiscounts);
        when(productPersistencePort.findById(eq(productId))).thenReturn(Optional.of(product));

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
        verify(productDTOMapper).toPriceDTO(eq(product), eq(percentageBasedDiscount),
                eq(quantityBasedDiscountToBeApplied), eq(productQuantity), any(), any());
        assertTrue(memoryAppender.contains(PRODUCT_FOUND_LOG_MSG.formatted(productId), Level.INFO));
        assertTrue(memoryAppender.contains(MATCHING_PERCENTAGE_BASED_DISCOUNT_LOG_MSG.formatted(productId, 10), Level.INFO));
        assertTrue(memoryAppender.contains(MATCHING_QUANTITY_BASED_DISCOUNT_LOG_MSG.formatted(productId, 15), Level.INFO));
        assertTrue(memoryAppender.contains(PRODUCT_PRICE_CALCULATED_LOG_MSG.formatted(productId, productQuantity, "2250.000 XXX", "750.000 XXX"), Level.INFO));
    }

    /**
     * Test purpose         - Verify if {@link ProductApplicationService#calculateProductPrice(UUID, int)}
     *                        returns {@link ProductPriceDTO} with proper data and quantity-based discount with
     *                        higher percentage rate applied when two quantity-based discounts' thresholds overlap.
     * Test data            - product id, product price and product quantity.
     * Test expected result - An instance of {@link ProductPriceDTO} with proper data is returned.
     * Test type            - Positive.
     */
    @Test
    void calculatePrice_shouldCalculatePriceAndApplyHigherQuantityBasedDiscount_whenProductIsFoundAndQuantityBasedDiscountsThresholdsOverlap() throws Exception {
        // Given
        UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        BigDecimal productPrice = new BigDecimal("1000.00");
        int productQuantity = 7;
        Currency currency = buildCurrency("EUR", 2);
        PercentageBasedDiscount percentageBasedDiscount = buildPercentageBasedDiscount(10);
        QuantityBasedDiscount quantityBasedDiscountToBeApplied = buildQuantityBasedDiscount(20, 6, null);
        Set<QuantityBasedDiscount> quantityBasedDiscounts = Set.of(
                quantityBasedDiscountToBeApplied,
                buildQuantityBasedDiscount(15, 6, 13)
        );
        Product product = buildProduct(productId, productPrice, currency,
                percentageBasedDiscount, quantityBasedDiscounts);
        when(productPersistencePort.findById(eq(productId))).thenReturn(Optional.of(product));

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
        verify(productDTOMapper).toPriceDTO(eq(product), eq(percentageBasedDiscount),
                eq(quantityBasedDiscountToBeApplied), eq(productQuantity), any(), any());
        assertTrue(memoryAppender.contains(PRODUCT_FOUND_LOG_MSG.formatted(productId), Level.INFO));
        assertTrue(memoryAppender.contains(MATCHING_PERCENTAGE_BASED_DISCOUNT_LOG_MSG.formatted(productId, 10), Level.INFO));
        assertTrue(memoryAppender.contains(MATCHING_QUANTITY_BASED_DISCOUNT_LOG_MSG.formatted(productId, 20), Level.INFO));
        assertTrue(memoryAppender.contains(PRODUCT_PRICE_CALCULATED_LOG_MSG.formatted(productId, productQuantity, "4900.00 EUR", "700.00 EUR"), Level.INFO));
    }

    /**
     * Test purpose         - Verify if {@link ProductApplicationService#calculateProductPrice(UUID, int)}
     *                        returns {@link ProductPriceDTO} with price equal to zero when discount rates sum is
     *                        more than 100%.
     * Test data            - product id, product price and product quantity.
     * Test expected result - An instance of {@link ProductPriceDTO} with proper data is returned.
     * Test type            - Positive.
     */
    @Test
    void calculatePrice_shouldCalculatePriceEqualToZero_whenProductIsFoundAndDiscountRatesSumIsMoreThan100Percent() throws Exception {
        // Given
        UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        BigDecimal productPrice = new BigDecimal("1000.00");
        int productQuantity = 3;
        Currency currency = buildCurrency("EUR", 2);
        PercentageBasedDiscount percentageBasedDiscount = buildPercentageBasedDiscount(90);
        QuantityBasedDiscount quantityBasedDiscountToBeApplied = buildQuantityBasedDiscount(15, 3, 5);
        Set<QuantityBasedDiscount> quantityBasedDiscounts = Set.of(
                quantityBasedDiscountToBeApplied,
                buildQuantityBasedDiscount(20, 6, null)
        );
        Product product = buildProduct(productId, productPrice, currency,
                percentageBasedDiscount, quantityBasedDiscounts);
        when(productPersistencePort.findById(eq(productId))).thenReturn(Optional.of(product));

        // When
        ProductPriceDTO productPriceDTO = productService.calculateProductPrice(productId, productQuantity);

        // Then
        assertNotNull(productPriceDTO);
        assertEquals(productId, productPriceDTO.getProductId());
        assertEquals(0, productPriceDTO.getBaseItemPrice().compareTo(productPrice));
        assertTrue(productPriceDTO.getAppliedDiscounts().stream().anyMatch(discount -> discount.getPercentageRate() == 90));
        assertTrue(productPriceDTO.getAppliedDiscounts().stream().anyMatch(discount -> discount.getPercentageRate() == 15));
        assertTrue(productPriceDTO.getAppliedDiscounts().stream().noneMatch(discount -> discount.getPercentageRate() == 20));
        assertEquals("0.00", productPriceDTO.getItemPrice().toString());
        assertEquals("0.00", productPriceDTO.getTotalPrice().toString());
        verify(productDTOMapper).toPriceDTO(eq(product), eq(percentageBasedDiscount),
                eq(quantityBasedDiscountToBeApplied), eq(productQuantity), any(), any());
        assertTrue(memoryAppender.contains(PRODUCT_FOUND_LOG_MSG.formatted(productId), Level.INFO));
        assertTrue(memoryAppender.contains(MATCHING_PERCENTAGE_BASED_DISCOUNT_LOG_MSG.formatted(productId, 90), Level.INFO));
        assertTrue(memoryAppender.contains(MATCHING_QUANTITY_BASED_DISCOUNT_LOG_MSG.formatted(productId, 15), Level.INFO));
        assertTrue(memoryAppender.contains(DISCOUNTS_SUM_EQUALS_TO_OR_MORE_THAN_100_PERCENT_LOG_MSG, Level.INFO));
        assertTrue(memoryAppender.contains(PRODUCT_PRICE_CALCULATED_LOG_MSG.formatted(productId, productQuantity, "0.00 EUR", "0.00 EUR"), Level.INFO));
    }

    /**
     * Test purpose         - Verify if {@link ProductApplicationService#calculateProductPrice(UUID, int)}
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
        when(productPersistencePort.findById(eq(productId))).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ProductNotFoundException.class, () -> productService.calculateProductPrice(productId, productQuantity));
        assertTrue(memoryAppender.contains(PRODUCT_NOT_FOUND_LOG_MSG.formatted(productId), Level.ERROR));
    }

    /**
     * Test purpose         - Verify if {@link ProductApplicationService#calculateProductPrice(UUID, int)}
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
        assertTrue(memoryAppender.contains(PRODUCT_QUANTITY_ERROR_LOG_MSG, Level.ERROR));
    }

}
