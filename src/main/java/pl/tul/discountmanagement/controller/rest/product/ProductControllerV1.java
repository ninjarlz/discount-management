package pl.tul.discountmanagement.controller.rest.product;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pl.tul.discountmanagement.exception.product.ProductNotFoundException;
import pl.tul.discountmanagement.mapper.product.ProductMapper;
import pl.tul.discountmanagement.model.dto.product.ProductDTO;
import pl.tul.discountmanagement.model.dto.product.ProductPriceDTO;
import pl.tul.discountmanagement.model.response.rest.product.ProductPriceResponseV1;
import pl.tul.discountmanagement.model.response.rest.product.ProductResponseV1;
import pl.tul.discountmanagement.service.product.ProductService;

import java.util.UUID;

import static pl.tul.discountmanagement.util.constant.rest.ApiUrls.PRICE_PATH_URL;
import static pl.tul.discountmanagement.util.constant.rest.ApiUrls.PRODUCT_ENDPOINT_V1;
import static pl.tul.discountmanagement.util.constant.rest.ApiUrls.PRODUCT_QUANTITY_REQUEST_PARAMETER;
import static pl.tul.discountmanagement.util.constant.security.Permissions.READ_PRICE_PERMISSION_EXPRESSION;
import static pl.tul.discountmanagement.util.constant.security.Permissions.READ_PRODUCT_PERMISSION_EXPRESSION;

/**
 * REST controller class exposing endpoints for reading product details.
 * API V1
 */
@RestController
@RequestMapping(PRODUCT_ENDPOINT_V1)
@RequiredArgsConstructor
@Validated
@Slf4j
public class ProductControllerV1 {

    private final ProductService productService;
    private final ProductMapper productMapper;

    /**
     * Handler for reading product details for given product id.
     */
    @GetMapping(value = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(READ_PRODUCT_PERMISSION_EXPRESSION)
    public ResponseEntity<ProductResponseV1> getProductById(@PathVariable("productId") UUID productId) {
        try {
            ProductDTO productDTO = productService.getProductById(productId);
            return ResponseEntity.ok(productMapper.DTOtoResponseV1(productDTO));
        } catch (ProductNotFoundException e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    /**
     * Handler for reading product price details for given product id and product quantity.
     */
    @GetMapping(value = "/{productId}/" + PRICE_PATH_URL, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(READ_PRICE_PERMISSION_EXPRESSION)
    public ResponseEntity<ProductPriceResponseV1> calculatePrice(@PathVariable("productId") UUID productId, @RequestParam(PRODUCT_QUANTITY_REQUEST_PARAMETER) @Min(1) int productQuantity) {
        try {
            ProductPriceDTO productPriceDTO = productService.calculateProductPrice(productId, productQuantity);
            return ResponseEntity.ok(productMapper.priceDTOtoPriceResponseV1(productPriceDTO));
        } catch (ProductNotFoundException e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }
}
