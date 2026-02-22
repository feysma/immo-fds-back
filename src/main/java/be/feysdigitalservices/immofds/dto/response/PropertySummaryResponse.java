package be.feysdigitalservices.immofds.dto.response;

import java.math.BigDecimal;

public record PropertySummaryResponse(
        String reference,
        String title,
        String propertyType,
        String transactionType,
        String status,
        BigDecimal price,
        Double surface,
        Integer bedrooms,
        Integer bathrooms,
        String city,
        String province,
        String energyRating,
        Long primaryImageId,
        Double latitude,
        Double longitude,
        String createdAt
) {}
