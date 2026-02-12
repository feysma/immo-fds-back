package be.feysdigitalservices.immofds.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record PropertyDetailResponse(
        String reference,
        String title,
        String description,
        String propertyType,
        String transactionType,
        String status,
        BigDecimal price,
        Double surface,
        Integer bedrooms,
        Integer bathrooms,
        Integer rooms,
        Integer floors,
        Integer constructionYear,
        String energyRating,
        boolean garden,
        boolean garage,
        boolean terrace,
        boolean basement,
        boolean elevator,
        boolean furnished,
        String street,
        String number,
        String postalCode,
        String city,
        String province,
        Double latitude,
        Double longitude,
        List<PropertyImageResponse> images,
        String createdAt,
        String updatedAt
) {}
