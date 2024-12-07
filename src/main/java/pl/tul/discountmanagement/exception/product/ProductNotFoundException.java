package pl.tul.discountmanagement.exception.product;

import java.util.UUID;

public class ProductNotFoundException extends Exception {
    private static final String MESSAGE = "Product with id '%s' not found";

    public ProductNotFoundException(UUID productId) {
        super(MESSAGE.formatted(productId));
    }
}
