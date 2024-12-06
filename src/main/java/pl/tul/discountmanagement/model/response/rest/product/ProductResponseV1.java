package pl.tul.discountmanagement.model.response.rest.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductResponseV1 {
    private UUID id;
}
