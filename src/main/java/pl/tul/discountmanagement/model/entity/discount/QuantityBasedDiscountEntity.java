package pl.tul.discountmanagement.model.entity.discount;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;
import org.hibernate.validator.constraints.Range;
import pl.tul.discountmanagement.model.entity.product.ProductEntity;

import java.util.Set;
import java.util.UUID;

import static pl.tul.discountmanagement.model.entity.product.ProductEntity.QUANTITY_BASED_DISCOUNTS;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "quantity_based_discount")
@Table(uniqueConstraints = @UniqueConstraint(
        name = "unique_lower_threshold_upper_threshold_percentage_rate",
        columnNames = {"lower_threshold", "upper_treshold", "percentage_rate"}
))
@Check(constraints = "upper_threshold IS NULL OR (lower_threshold < upper_threshold)")
public class QuantityBasedDiscountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private int lowerThreshold;
    private int upperThreshold;
    @Column(nullable = false)
    @Range(min = 0, max = 100)
    private int percentageRate;
    @ManyToMany(mappedBy = QUANTITY_BASED_DISCOUNTS)
    private Set<ProductEntity> products;
}
