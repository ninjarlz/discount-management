package pl.tul.discountmanagement.model.entity.product;

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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import pl.tul.discountmanagement.model.entity.currency.CurrencyEntity;
import pl.tul.discountmanagement.model.entity.discount.PercentageBasedDiscountEntity;
import pl.tul.discountmanagement.model.entity.discount.QuantityBasedDiscountEntity;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import static pl.tul.discountmanagement.model.entity.discount.QuantityBasedDiscountEntity.PERCENTAGE_RATE;

/**
 * The Entity class for the Product item.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "product")
public class ProductEntity {

    public static final String CURRENCY = "currency";
    public static final String PERCENTAGE_BASED_DISCOUNT = "percentageBasedDiscount";
    public static final String QUANTITY_BASED_DISCOUNTS = "quantityBasedDiscounts";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String description;
    @ManyToOne
    @JoinColumn(name = "currency_id", nullable = false)
    private CurrencyEntity currency;
    @Column(nullable = false)
    private BigDecimal price;
    @ManyToOne
    @JoinColumn(name = "percentage_based_discount_id")
    private PercentageBasedDiscountEntity percentageBasedDiscount;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "product_quantity_based_discount",
            joinColumns = { @JoinColumn(name = "product_id") },
            inverseJoinColumns = { @JoinColumn(name = "quantity_based_discount_id") }
    )
    @OrderBy(PERCENTAGE_RATE)
    private Set<QuantityBasedDiscountEntity> quantityBasedDiscounts = new LinkedHashSet<>();
}
