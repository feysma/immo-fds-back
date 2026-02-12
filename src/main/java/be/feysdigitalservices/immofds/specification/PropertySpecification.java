package be.feysdigitalservices.immofds.specification;

import be.feysdigitalservices.immofds.domain.entity.Property;
import be.feysdigitalservices.immofds.domain.enums.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public final class PropertySpecification {

    private PropertySpecification() {}

    public static Specification<Property> hasStatus(PropertyStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Property> hasPropertyType(PropertyType type) {
        return (root, query, cb) -> type == null ? null : cb.equal(root.get("propertyType"), type);
    }

    public static Specification<Property> hasTransactionType(TransactionType type) {
        return (root, query, cb) -> type == null ? null : cb.equal(root.get("transactionType"), type);
    }

    public static Specification<Property> hasProvince(Province province) {
        return (root, query, cb) -> province == null ? null : cb.equal(root.get("province"), province);
    }

    public static Specification<Property> hasCityLike(String city) {
        return (root, query, cb) -> city == null || city.isBlank()
                ? null
                : cb.like(cb.lower(root.get("city")), "%" + city.toLowerCase() + "%");
    }

    public static Specification<Property> hasPriceGreaterThanOrEqual(BigDecimal minPrice) {
        return (root, query, cb) -> minPrice == null
                ? null
                : cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<Property> hasPriceLessThanOrEqual(BigDecimal maxPrice) {
        return (root, query, cb) -> maxPrice == null
                ? null
                : cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    public static Specification<Property> hasSurfaceGreaterThanOrEqual(Double minSurface) {
        return (root, query, cb) -> minSurface == null
                ? null
                : cb.greaterThanOrEqualTo(root.get("surface"), minSurface);
    }

    public static Specification<Property> hasSurfaceLessThanOrEqual(Double maxSurface) {
        return (root, query, cb) -> maxSurface == null
                ? null
                : cb.lessThanOrEqualTo(root.get("surface"), maxSurface);
    }

    public static Specification<Property> hasMinBedrooms(Integer minBedrooms) {
        return (root, query, cb) -> minBedrooms == null
                ? null
                : cb.greaterThanOrEqualTo(root.get("bedrooms"), minBedrooms);
    }

    public static Specification<Property> hasEnergyRating(EnergyRating rating) {
        return (root, query, cb) -> rating == null ? null : cb.equal(root.get("energyRating"), rating);
    }

    public static Specification<Property> hasGarden(Boolean garden) {
        return (root, query, cb) -> garden == null ? null : cb.equal(root.get("garden"), garden);
    }

    public static Specification<Property> hasGarage(Boolean garage) {
        return (root, query, cb) -> garage == null ? null : cb.equal(root.get("garage"), garage);
    }

    public static Specification<Property> hasTerrace(Boolean terrace) {
        return (root, query, cb) -> terrace == null ? null : cb.equal(root.get("terrace"), terrace);
    }

    public static Specification<Property> hasBasement(Boolean basement) {
        return (root, query, cb) -> basement == null ? null : cb.equal(root.get("basement"), basement);
    }

    public static Specification<Property> hasElevator(Boolean elevator) {
        return (root, query, cb) -> elevator == null ? null : cb.equal(root.get("elevator"), elevator);
    }

    public static Specification<Property> hasFurnished(Boolean furnished) {
        return (root, query, cb) -> furnished == null ? null : cb.equal(root.get("furnished"), furnished);
    }
}
