package be.feysdigitalservices.immofds.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ContactNoteCreateRequest(
        @NotBlank(message = "Le contenu de la note est obligatoire")
        String content
) {}
