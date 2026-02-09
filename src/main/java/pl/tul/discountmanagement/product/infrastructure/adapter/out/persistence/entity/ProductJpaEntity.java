package pl.tul.discountmanagement.product.infrastructure.adapter.out.persistence.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderBy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import static pl.tul.discountmanagement.product.infrastructure.adapter.out.persistence.entity.QuantityBasedDiscountJpaEntity.PERCENTAGE_RATE;

/**
 * The Entity class for the product item.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "product")
public class ProductJpaEntity {

    public static final String CURRENCY = "currency";
    public static final String PERCENTAGE_BASED_DISCOUNT = "percentageBasedDiscount";
    public static final String QUANTITY_BASED_DISCOUNTS = "quantityBasedDiscounts";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Nullable
    private String name;

    @Nullable
    private String description;

    @ManyToOne
    @JoinColumn(name = "currency_id", nullable = false)
    private CurrencyJpaEntity currency;

    @Column(nullable = false)
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "percentage_based_discount_id")
    @Nullable
    private PercentageBasedDiscountJpaEntity percentageBasedDiscount;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "product_quantity_based_discount",
            joinColumns = { @JoinColumn(name = "product_id") },
            inverseJoinColumns = { @JoinColumn(name = "quantity_based_discount_id") }
    )
    @OrderBy(PERCENTAGE_RATE)
    @Builder.Default
    @Nullable
    private Set<QuantityBasedDiscountJpaEntity> quantityBasedDiscounts = new LinkedHashSet<>();
}
