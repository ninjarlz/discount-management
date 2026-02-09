package pl.tul.discountmanagement.shared.infrastructure.security.dto;

import jakarta.annotation.Nullable;
import lombok.Builder;

import java.util.UUID;

/**
 * Object allows storing user details obtained from JWT.
 */
@Builder
public record UserDetailsDTO(UUID userId, @Nullable String username, @Nullable String email) {
}
