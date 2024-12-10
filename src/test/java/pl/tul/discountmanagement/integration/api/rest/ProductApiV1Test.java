package pl.tul.discountmanagement.integration.api.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.tul.discountmanagement.model.response.rest.product.ProductPriceResponseV1;
import pl.tul.discountmanagement.model.response.rest.product.ProductResponseV1;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.tul.discountmanagement.util.constant.TestConstants.DETAIL_ERROR_ENTRY;
import static pl.tul.discountmanagement.util.constant.TestConstants.INVALID_PRODUCT_ID_RESPONSE_MSG;
import static pl.tul.discountmanagement.util.constant.TestConstants.INVALID_PRODUCT_QUANTITY_RESPONSE_MSG;
import static pl.tul.discountmanagement.util.constant.TestConstants.MESSAGE_ERROR_ENTRY;
import static pl.tul.discountmanagement.util.constant.TestConstants.PERCENTAGE_BASED_DISCOUNT_ID;
import static pl.tul.discountmanagement.util.constant.TestConstants.PRODUCT_ID;
import static pl.tul.discountmanagement.util.constant.TestConstants.PRODUCT_NOT_FOUND_RESPONSE_MSG;
import static pl.tul.discountmanagement.util.constant.TestConstants.QUANTITY_BASED_DISCOUNT_ID_1;
import static pl.tul.discountmanagement.util.constant.TestConstants.QUANTITY_BASED_DISCOUNT_ID_2;
import static pl.tul.discountmanagement.util.constant.config.ApplicationProfiles.INTEGRATION_TEST_PROFILE;
import static pl.tul.discountmanagement.util.constant.rest.ApiUrls.PRICE_PATH_URL;
import static pl.tul.discountmanagement.util.constant.rest.ApiUrls.PRODUCT_ENDPOINT_V1;
import static pl.tul.discountmanagement.util.constant.rest.ApiUrls.PRODUCT_QUANTITY_REQUEST_PARAMETER;
import static pl.tul.discountmanagement.util.constant.security.Permissions.READ_PRICE_PERMISSION;
import static pl.tul.discountmanagement.util.constant.security.Permissions.READ_PRODUCT_PERMISSION;

/**
 * Test class for product API V1.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(INTEGRATION_TEST_PROFILE)
class ProductApiV1Test {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Test purpose         - Verify if GET /v1/product/{productId} endpoint
     *                        returns {@link ProductResponseV1} with proper data.
     * Test data            - endpoint url.
     * Test expected result - An instance of {@link ProductResponseV1} with proper data is returned.
     * Test type            - Positive.
     */
    @Test
    @WithMockUser(authorities = READ_PRODUCT_PERMISSION)
    void getProductById_shouldReturnOKResponse_whenProductIsFound() throws Exception {
        // Given
        String url = PRODUCT_ENDPOINT_V1 + "/" + PRODUCT_ID;

        // When
        MvcResult mvcGetResult = mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn();

        // Then
        String body = mvcGetResult.getResponse().getContentAsString();
        ProductResponseV1 productResponse = objectMapper.readValue(body, ProductResponseV1.class);
        assertNotNull(productResponse);
        assertEquals(PRODUCT_ID, productResponse.getId());
        assertEquals(3, productResponse.getDiscounts().size());
        assertTrue(productResponse.getDiscounts().stream().anyMatch(discount -> discount.getId().equals(PERCENTAGE_BASED_DISCOUNT_ID)));
        assertTrue(productResponse.getDiscounts().stream().anyMatch(discount -> discount.getId().equals(QUANTITY_BASED_DISCOUNT_ID_1)));
        assertTrue(productResponse.getDiscounts().stream().anyMatch(discount -> discount.getId().equals(QUANTITY_BASED_DISCOUNT_ID_2)));
        assertEquals("2999.99", productResponse.getPrice().toString());
        assertEquals("USD", productResponse.getCurrency());
    }

    /**
     * Test purpose         - Verify if GET /v1/product/{productId} endpoint
     *                        returns 404 error response when product is not found.
     * Test data            - product id, endpoint url.
     * Test expected result - 404 error response is returned.
     * Test type            - Negative.
     */
    @Test
    @WithMockUser(authorities = READ_PRODUCT_PERMISSION)
    void getProductById_shouldReturnNotFoundResponse_whenProductIsNotFound() throws Exception {
        // Given
        UUID dummyProductId = UUID.randomUUID();
        String url = PRODUCT_ENDPOINT_V1 + "/" + dummyProductId;

        // When
        MvcResult mvcGetResult = mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound())
                        .andReturn();

        // Then
        String body = mvcGetResult.getResponse().getContentAsString();
        Map<String, Object> response = objectMapper.readValue(body, new TypeReference<>() {});
        assertNotNull(response);
        String errorMsg = (String) response.get(DETAIL_ERROR_ENTRY);
        assertEquals(PRODUCT_NOT_FOUND_RESPONSE_MSG.formatted(dummyProductId), errorMsg);
    }

    /**
     * Test purpose         - Verify if GET /v1/product/{productId} endpoint
     *                        returns 400 error response when product id is invalid.
     * Test data            - product id, endpoint url.
     * Test expected result - 400 error response is returned.
     * Test type            - Negative.
     */
    @Test
    @WithMockUser(authorities = READ_PRODUCT_PERMISSION)
    void getProductById_shouldReturnBadRequestResponse_whenProductIdIsInvalid() throws Exception {
        // Given
        String dummyProductId = "AAA";
        String url = PRODUCT_ENDPOINT_V1 + "/" + dummyProductId;

        // When
        MvcResult mvcGetResult = mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn();

        // Then
        String body = mvcGetResult.getResponse().getContentAsString();
        Map<String, Object> response = objectMapper.readValue(body, new TypeReference<>() {});
        assertNotNull(response);
        String errorMsg = (String) response.get(DETAIL_ERROR_ENTRY);
        assertEquals(INVALID_PRODUCT_ID_RESPONSE_MSG.formatted(dummyProductId), errorMsg);
    }

    /**
     * Test purpose         - Verify if GET /v1/product/{productId} endpoint
     *                        returns 403 error response when given JWT token does not contain required permission.
     * Test data            - endpoint url.
     * Test expected result - 403 error response is returned.
     * Test type            - Negative.
     */
    @Test
    @WithMockUser(authorities = {"DUMMY_PERMISSION_1", "DUMMY_PERMISSION_2"})
    void getProductById_shouldReturnForbiddenResponse_whenNoPermission() throws Exception {
        // Given
        String url = PRODUCT_ENDPOINT_V1 + "/" + PRODUCT_ID;

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isForbidden())
                        .andReturn();
    }

    /**
     * Test purpose         - Verify if GET /v1/product/{productId} endpoint
     *                        returns 401 error response when given request does not contain valid JWT token.
     * Test data            - endpoint url.
     * Test expected result - 401 error response is returned.
     * Test type            - Negative.
     */
    @Test
    void getProductById_shouldReturnUnauthorizedResponse_whenNoTokenProvided() throws Exception {
        // Given
        String url = PRODUCT_ENDPOINT_V1 + "/" + PRODUCT_ID;

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    /**
     * Test purpose         - Verify if GET /v1/product/{productId}/price?productQuantity={productQuantity} endpoint
     *                        returns {@link ProductPriceResponseV1} with proper data.
     * Test data            - product quantity, endpoint url.
     * Test expected result - An instance of {@link ProductPriceResponseV1} with proper data is returned.
     * Test type            - Positive.
     */
    @Test
    @WithMockUser(authorities = READ_PRICE_PERMISSION)
    void calculatePrice_shouldReturnOKResponse_whenProductIsFound() throws Exception {
        // Given
        int productQuantity = 3;
        String url = PRODUCT_ENDPOINT_V1 + "/" + PRODUCT_ID + "/" + PRICE_PATH_URL + "?" + PRODUCT_QUANTITY_REQUEST_PARAMETER + "=" + productQuantity;

        // When
        MvcResult mvcGetResult = mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn();

        // Then
        String body = mvcGetResult.getResponse().getContentAsString();
        ProductPriceResponseV1 productPriceResponse = objectMapper.readValue(body, ProductPriceResponseV1.class);
        assertNotNull(productPriceResponse);
        assertEquals(PRODUCT_ID, productPriceResponse.getProductId());
        assertEquals(productQuantity, productPriceResponse.getProductQuantity());
        assertEquals(2, productPriceResponse.getAppliedDiscounts().size());
        assertTrue(productPriceResponse.getAppliedDiscounts().stream().anyMatch(discount -> discount.getId().equals(PERCENTAGE_BASED_DISCOUNT_ID)));
        assertTrue(productPriceResponse.getAppliedDiscounts().stream().anyMatch(discount -> discount.getId().equals(QUANTITY_BASED_DISCOUNT_ID_1)));
        assertTrue(productPriceResponse.getAppliedDiscounts().stream().noneMatch(discount -> discount.getId().equals(QUANTITY_BASED_DISCOUNT_ID_2)));
        assertEquals("2999.99", productPriceResponse.getBaseItemPrice().toString());
        assertEquals("7199.98", productPriceResponse.getTotalPrice().toString());
        assertEquals("2399.99", productPriceResponse.getItemPrice().toString());
        assertEquals("USD", productPriceResponse.getCurrency());
    }

    /**
     * Test purpose         - Verify if GET /v1/product/{productId}/price?productQuantity={productQuantity} endpoint
     *                        returns {@link ProductPriceResponseV1} with proper data and only percentage-based discount
     *                        applied when product quantity is not high enough.
     * Test data            - product quantity, endpoint url.
     * Test expected result - An instance of {@link ProductPriceResponseV1} with proper data is returned.
     * Test type            - Positive.
     */
    @Test
    @WithMockUser(authorities = READ_PRICE_PERMISSION)
    void calculatePrice_shouldReturnOKResponseAndApplyOnlyPercentageBasedDiscount_whenProductIsFoundAndProductQuantityIsNotHighEnough() throws Exception {
        // Given
        int productQuantity = 1;
        String url = PRODUCT_ENDPOINT_V1 + "/" + PRODUCT_ID + "/" + PRICE_PATH_URL + "?" + PRODUCT_QUANTITY_REQUEST_PARAMETER + "=" + productQuantity;

        // When
        MvcResult mvcGetResult = mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String body = mvcGetResult.getResponse().getContentAsString();
        ProductPriceResponseV1 productPriceResponse = objectMapper.readValue(body, ProductPriceResponseV1.class);
        assertNotNull(productPriceResponse);
        assertEquals(PRODUCT_ID, productPriceResponse.getProductId());
        assertEquals(productQuantity, productPriceResponse.getProductQuantity());
        assertEquals(1, productPriceResponse.getAppliedDiscounts().size());
        assertTrue(productPriceResponse.getAppliedDiscounts().stream().anyMatch(discount -> discount.getId().equals(PERCENTAGE_BASED_DISCOUNT_ID)));
        assertTrue(productPriceResponse.getAppliedDiscounts().stream().noneMatch(discount -> discount.getId().equals(QUANTITY_BASED_DISCOUNT_ID_1)));
        assertTrue(productPriceResponse.getAppliedDiscounts().stream().noneMatch(discount -> discount.getId().equals(QUANTITY_BASED_DISCOUNT_ID_2)));
        assertEquals("2999.99", productPriceResponse.getBaseItemPrice().toString());
        assertEquals("2699.99", productPriceResponse.getTotalPrice().toString());
        assertEquals("2699.99", productPriceResponse.getItemPrice().toString());
        assertEquals("USD", productPriceResponse.getCurrency());
    }

    /**
     * Test purpose         - Verify if GET /v1/product/{productId}/price?productQuantity={productQuantity} endpoint
     *                        returns {@link ProductPriceResponseV1} with proper data and quantity-based discount with
     *                        higher percentage rate applied.
     * Test data            - product quantity, endpoint url.
     * Test expected result - An instance of {@link ProductPriceResponseV1} with proper data is returned.
     * Test type            - Positive.
     */
    @Test
    @WithMockUser(authorities = READ_PRICE_PERMISSION)
    void calculatePrice_shouldReturnOKResponseAndApplyHigherQuantityBasedDiscount_whenProductIsFoundAndProductQuantityHasAppropriateValue() throws Exception {
        // Given
        int productQuantity = 13;
        String url = PRODUCT_ENDPOINT_V1 + "/" + PRODUCT_ID + "/" + PRICE_PATH_URL + "?" + PRODUCT_QUANTITY_REQUEST_PARAMETER + "=" + productQuantity;

        // When
        MvcResult mvcGetResult = mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String body = mvcGetResult.getResponse().getContentAsString();
        ProductPriceResponseV1 productPriceResponse = objectMapper.readValue(body, ProductPriceResponseV1.class);
        assertNotNull(productPriceResponse);
        assertEquals(PRODUCT_ID, productPriceResponse.getProductId());
        assertEquals(productQuantity, productPriceResponse.getProductQuantity());
        assertEquals(2, productPriceResponse.getAppliedDiscounts().size());
        assertTrue(productPriceResponse.getAppliedDiscounts().stream().anyMatch(discount -> discount.getId().equals(PERCENTAGE_BASED_DISCOUNT_ID)));
        assertTrue(productPriceResponse.getAppliedDiscounts().stream().noneMatch(discount -> discount.getId().equals(QUANTITY_BASED_DISCOUNT_ID_1)));
        assertTrue(productPriceResponse.getAppliedDiscounts().stream().anyMatch(discount -> discount.getId().equals(QUANTITY_BASED_DISCOUNT_ID_2)));
        assertEquals("2999.99", productPriceResponse.getBaseItemPrice().toString());
        assertEquals("23399.92", productPriceResponse.getTotalPrice().toString());
        assertEquals("1799.99", productPriceResponse.getItemPrice().toString());
        assertEquals("USD", productPriceResponse.getCurrency());
    }

    /**
     * Test purpose         - Verify if GET /v1/product/{productId}/price?productQuantity={productQuantity} endpoint
     *                        returns 404 error response when product is not found.
     * Test data            - product id, product quantity, endpoint url.
     * Test expected result - 404 error response is returned.
     * Test type            - Negative.
     */
    @Test
    @WithMockUser(authorities = READ_PRICE_PERMISSION)
    void calculatePrice_shouldReturnNotFoundResponse_whenProductIsNotFound() throws Exception {
        // Given
        UUID dummyProductId = UUID.randomUUID();
        int productQuantity = 3;
        String url = PRODUCT_ENDPOINT_V1 + "/" + dummyProductId + "/" + PRICE_PATH_URL + "?" + PRODUCT_QUANTITY_REQUEST_PARAMETER + "=" + productQuantity;

        // When
        MvcResult mvcGetResult = mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound())
                        .andReturn();

        // Then
        String body = mvcGetResult.getResponse().getContentAsString();
        Map<String, Object> response = objectMapper.readValue(body, new TypeReference<>() {});
        assertNotNull(response);
        String errorMsg = (String) response.get(DETAIL_ERROR_ENTRY);
        assertEquals(PRODUCT_NOT_FOUND_RESPONSE_MSG.formatted(dummyProductId), errorMsg);
    }

    /**
     * Test purpose         - Verify if GET /v1/product/{productId}/price?productQuantity={productQuantity} endpoint
     *                        returns 400 error response when product id is invalid.
     * Test data            - product id, product quantity, endpoint url.
     * Test expected result - 400 error response is returned.
     * Test type            - Negative.
     */
    @Test
    @WithMockUser(authorities = READ_PRICE_PERMISSION)
    void calculatePrice_shouldReturnBadRequestResponse_whenProductIdIsInvalid() throws Exception {
        // Given
        String dummyProductId = "AAA";
        int productQuantity = 3;
        String url = PRODUCT_ENDPOINT_V1 + "/" + dummyProductId + "/" + PRICE_PATH_URL + "?" + PRODUCT_QUANTITY_REQUEST_PARAMETER + "=" + productQuantity;

        // When
        MvcResult mvcGetResult = mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn();

        // Then
        String body = mvcGetResult.getResponse().getContentAsString();
        Map<String, Object> response = objectMapper.readValue(body, new TypeReference<>() {});
        assertNotNull(response);
        String errorMsg = (String) response.get(DETAIL_ERROR_ENTRY);
        assertEquals(INVALID_PRODUCT_ID_RESPONSE_MSG.formatted(dummyProductId), errorMsg);
    }

    /**
     * Test purpose         - Verify if GET /v1/product/{productId}/price?productQuantity={productQuantity} endpoint
     *                        returns 400 error response when product quantity is invalid.
     * Test data            - product quantity, endpoint url.
     * Test expected result - 400 error response is returned.
     * Test type            - Negative.
     */
    @Test
    @WithMockUser(authorities = READ_PRICE_PERMISSION)
    void calculatePrice_shouldReturnBadRequestResponse_whenProductQuantityIsInvalid() throws Exception {
        // Given
        int productQuantity = 0;
        String url = PRODUCT_ENDPOINT_V1 + "/" + PRODUCT_ID + "/" + PRICE_PATH_URL + "?" + PRODUCT_QUANTITY_REQUEST_PARAMETER + "=" + productQuantity;

        // When
        MvcResult mvcGetResult = mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        // Then
        String body = mvcGetResult.getResponse().getContentAsString();
        Map<String, Object> response = objectMapper.readValue(body, new TypeReference<>() {});
        assertNotNull(response);
        String errorMsg = (String) response.get(MESSAGE_ERROR_ENTRY);
        assertEquals(INVALID_PRODUCT_QUANTITY_RESPONSE_MSG, errorMsg);
    }

    /**
     * Test purpose         - Verify if GET /v1/product/{productId}/price?productQuantity={productQuantity} endpoint
     *                        returns 403 error response when given JWT token does not contain required permission.
     * Test data            - product quantity, endpoint url.
     * Test expected result - 403 error response is returned.
     * Test type            - Negative.
     */
    @Test
    @WithMockUser(authorities = {READ_PRODUCT_PERMISSION, "DUMMY_PERMISSION_3"})
    void calculatePrice_shouldReturnForbiddenResponse_whenNoPermission() throws Exception {
        // Given
        int productQuantity = 3;
        String url = PRODUCT_ENDPOINT_V1 + "/" + PRODUCT_ID + "/" + PRICE_PATH_URL + "?" + PRODUCT_QUANTITY_REQUEST_PARAMETER + "=" + productQuantity;

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isForbidden())
                        .andReturn();
    }

    /**
     * Test purpose         - Verify if GET /v1/product/{productId}/price?productQuantity={productQuantity} endpoint
     *                        returns 401 error response when given request does not contain valid JWT token.
     * Test data            - product quantity, endpoint url.
     * Test expected result - 401 error response is returned.
     * Test type            - Negative.
     */
    @Test
    void calculatePrice_shouldReturnUnauthorizedResponse_whenNoTokenProvided() throws Exception {
        // Given
        int productQuantity = 3;
        String url = PRODUCT_ENDPOINT_V1 + "/" + PRODUCT_ID + "/" + PRICE_PATH_URL + "?" + PRODUCT_QUANTITY_REQUEST_PARAMETER + "=" + productQuantity;

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }
}
