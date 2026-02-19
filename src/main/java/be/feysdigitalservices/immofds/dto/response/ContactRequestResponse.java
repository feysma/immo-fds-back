package be.feysdigitalservices.immofds.dto.response;

import java.math.BigDecimal;
import java.util.List;

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
        List<ContactNoteResponse> notes,
        String createdAt,
        String updatedAt
) {}
