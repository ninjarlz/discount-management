package pl.tul.discountmanagement.controller.rest.product;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.tul.discountmanagement.model.response.rest.product.ProductResponseV1;

import java.util.UUID;

import static pl.tul.discountmanagement.util.constant.rest.ApiUrls.PRODUCT_ENDPOINT_V1;
import static pl.tul.discountmanagement.util.constant.security.Permissions.READ_PRODUCT_PERMISSION_EXPRESSION;

@RestController
@RequestMapping(PRODUCT_ENDPOINT_V1)
@RequiredArgsConstructor
@Validated
public class ProductControllerV1 {

    @GetMapping(value = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(READ_PRODUCT_PERMISSION_EXPRESSION)
    public ResponseEntity<ProductResponseV1> getProductById(@PathVariable("productId") UUID productId) {
        return ResponseEntity.ok(new ProductResponseV1(productId));
    }
}
