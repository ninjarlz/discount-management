package pl.tul.discountmanagement.api.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.transaction.annotation.Transactional;
import pl.tul.discountmanagement.model.entity.currency.CurrencyEntity;
import pl.tul.discountmanagement.model.entity.discount.PercentageBasedDiscountEntity;
import pl.tul.discountmanagement.model.entity.discount.QuantityBasedDiscountEntity;
import pl.tul.discountmanagement.model.entity.product.ProductEntity;
import pl.tul.discountmanagement.repository.product.ProductRepository;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.tul.discountmanagement.util.TestDataUtils.buildCurrencyEntity;
import static pl.tul.discountmanagement.util.TestDataUtils.buildPercentageBasedDiscountEntity;
import static pl.tul.discountmanagement.util.TestDataUtils.buildProductEntity;
import static pl.tul.discountmanagement.util.TestDataUtils.buildQuantityBasedDiscountEntity;
import static pl.tul.discountmanagement.util.constant.config.ApplicationProfiles.INTEGRATION_TEST_PROFILE;
import static pl.tul.discountmanagement.util.constant.rest.ApiUrls.PRODUCT_ENDPOINT_V1;
import static pl.tul.discountmanagement.util.constant.security.Permissions.READ_PRODUCT_PERMISSION;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(INTEGRATION_TEST_PROFILE)
class ProductApiV1Test {

    private static final UUID PRODUCT_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    @Transactional
    void setUp() {
        BigDecimal productPrice = new BigDecimal("1000.00");
        CurrencyEntity currencyEntity = buildCurrencyEntity("EUR", 2);
        PercentageBasedDiscountEntity percentageBasedDiscountEntity = buildPercentageBasedDiscountEntity(10);
        Set<QuantityBasedDiscountEntity> quantityBasedDiscountEntities = Set.of(
                buildQuantityBasedDiscountEntity(15, 3, 5),
                buildQuantityBasedDiscountEntity(20, 6, null)
        );
        ProductEntity productEntity = buildProductEntity(PRODUCT_ID, productPrice, currencyEntity,
                percentageBasedDiscountEntity, quantityBasedDiscountEntities);
        productRepository.save(productEntity);
    }

    @Test
    @WithMockUser(authorities = READ_PRODUCT_PERMISSION)
    void getProductById_shouldReturnOKResponse_whenProductIsFound() throws Exception {
        // GET - OK
        MvcResult mvcGetResult = mockMvc.perform(MockMvcRequestBuilders.get(PRODUCT_ENDPOINT_V1 + "/"  + PRODUCT_ID)
                        .servletPath(PRODUCT_ENDPOINT_V1 + "/"  + PRODUCT_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String body = mvcGetResult.getResponse().getContentAsString();
    }

}
