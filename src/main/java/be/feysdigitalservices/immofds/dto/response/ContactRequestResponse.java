package be.feysdigitalservices.immofds.dto.response;

import java.math.BigDecimal;

public record ContactRequestResponse(
        Long id,
        String contactType,
        String status,
        String firstName,
        String lastName,
        String email,
        String phone,
        String message,
        String propertyReference,
        String propertyAddress,
        String propertyType,
        BigDecimal estimatedPrice,
        String adminNotes,
        String createdAt,
        String updatedAt
) {}
