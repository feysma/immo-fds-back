package be.feysdigitalservices.immofds.dto.request;

import be.feysdigitalservices.immofds.domain.enums.*;

import java.math.BigDecimal;

public record PropertySearchCriteria(
        PropertyType propertyType,
        TransactionType transactionType,
        Province province,
        String city,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Double minSurface,
        Double maxSurface,
        Integer minBedrooms,
        EnergyRating energyRating,
        Boolean garden,
        Boolean garage,
        Boolean terrace,
        Boolean basement,
        Boolean elevator,
        Boolean furnished
) {}
