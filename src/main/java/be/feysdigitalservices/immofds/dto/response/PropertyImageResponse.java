package be.feysdigitalservices.immofds.dto.response;

public record PropertyImageResponse(
        Long id,
        String fileName,
        String contentType,
        int displayOrder,
        boolean isPrimary
) {}
