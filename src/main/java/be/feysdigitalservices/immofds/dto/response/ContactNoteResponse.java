package be.feysdigitalservices.immofds.dto.response;

public record ContactNoteResponse(
        Long id,
        String content,
        Long authorId,
        String authorName,
        String createdAt,
        String updatedAt
) {}
