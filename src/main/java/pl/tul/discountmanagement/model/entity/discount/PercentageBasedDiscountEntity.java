package pl.tul.discountmanagement.model.entity.discount;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import pl.tul.discountmanagement.model.entity.product.ProductEntity;

import java.util.Set;
import java.util.UUID;

import static pl.tul.discountmanagement.model.entity.product.ProductEntity.PERCENTAGE_BASED_DISCOUNT;

/**
 * The Entity class for the percentage-based discount item.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "percentage_based_discount")
public class PercentageBasedDiscountEntity  {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    @Range(min = 0, max = 100)
    private int percentageRate;

    @OneToMany(mappedBy = PERCENTAGE_BASED_DISCOUNT)
    @Nullable
    private Set<ProductEntity> products;
}
