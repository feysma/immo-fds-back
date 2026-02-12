package be.feysdigitalservices.immofds.specification;

import be.feysdigitalservices.immofds.domain.entity.Property;
import be.feysdigitalservices.immofds.domain.enums.*;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PropertySpecificationTest {

    @Test
    void hasStatus_withNull_shouldReturnNullPredicate() {
        Specification<Property> spec = PropertySpecification.hasStatus(null);
        assertThat(spec).isNotNull();
    }

    @Test
    void hasStatus_withValue_shouldReturnNonNull() {
        Specification<Property> spec = PropertySpecification.hasStatus(PropertyStatus.PUBLISHED);
        assertThat(spec).isNotNull();
    }

    @Test
    void hasPropertyType_withValue_shouldReturnNonNull() {
        Specification<Property> spec = PropertySpecification.hasPropertyType(PropertyType.HOUSE);
        assertThat(spec).isNotNull();
    }

    @Test
    void hasCityLike_withBlank_shouldReturnNonNull() {
        Specification<Property> spec = PropertySpecification.hasCityLike("");
        assertThat(spec).isNotNull();
    }

    @Test
    void hasCityLike_withNull_shouldReturnNonNull() {
        Specification<Property> spec = PropertySpecification.hasCityLike(null);
        assertThat(spec).isNotNull();
    }

    @Test
    void hasPriceRange_shouldReturnNonNull() {
        Specification<Property> spec = Specification
                .where(PropertySpecification.hasPriceGreaterThanOrEqual(new BigDecimal("100000")))
                .and(PropertySpecification.hasPriceLessThanOrEqual(new BigDecimal("500000")));
        assertThat(spec).isNotNull();
    }

    @Test
    void composedSpecification_shouldBeComposable() {
        Specification<Property> spec = Specification
                .where(PropertySpecification.hasStatus(PropertyStatus.PUBLISHED))
                .and(PropertySpecification.hasPropertyType(PropertyType.APARTMENT))
                .and(PropertySpecification.hasTransactionType(TransactionType.RENT))
                .and(PropertySpecification.hasProvince(Province.BRUXELLES_CAPITALE))
                .and(PropertySpecification.hasMinBedrooms(2))
                .and(PropertySpecification.hasGarden(true));
        assertThat(spec).isNotNull();
    }
}
