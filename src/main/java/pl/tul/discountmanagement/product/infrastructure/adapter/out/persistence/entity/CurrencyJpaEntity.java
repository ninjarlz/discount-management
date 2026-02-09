package pl.tul.discountmanagement.product.infrastructure.adapter.out.persistence.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.Set;
import java.util.UUID;

/**
 * The Entity class for the currency item.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "currency")
public class CurrencyJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String currencyCode;

    @Column(nullable = false)
    @Min(1)
    private int fractionDigits;

    @OneToMany(mappedBy = ProductJpaEntity.CURRENCY)
    @Fetch(FetchMode.SUBSELECT)
    @Nullable
    private Set<ProductJpaEntity> products;
}
