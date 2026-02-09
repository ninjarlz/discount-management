package pl.tul.discountmanagement.product.infrastructure.adapter.out.persistence.entity;

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

import java.util.Set;
import java.util.UUID;

/**
 * The Entity class for the percentage-based discount item.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "percentage_based_discount")
public class PercentageBasedDiscountJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    @Range(min = 0, max = 100)
    private int percentageRate;

    @OneToMany(mappedBy = ProductJpaEntity.PERCENTAGE_BASED_DISCOUNT)
    @Nullable
    private Set<ProductJpaEntity> products;
}
