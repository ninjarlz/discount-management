package pl.tul.discountmanagement.product.domain.model;

import java.util.UUID;
import lombok.Builder;

/**
 * Domain value object representing a currency.
 */
@Builder
public record Currency(UUID id, String currencyCode, int fractionDigits) {}
