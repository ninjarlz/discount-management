package pl.tul.discountmanagement.exception.product;

import java.util.UUID;

/**
 * Checked {@link Exception} indicating that product with given id is not found.
 */
public class ProductNotFoundException extends Exception {
    private static final String MESSAGE = "Product with id '%s' not found";

    public ProductNotFoundException(UUID productId) {
        super(MESSAGE.formatted(productId));
    }
}
