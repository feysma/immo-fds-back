package be.feysdigitalservices.immofds.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ImageReorderRequest(
        @NotEmpty(message = "La liste des identifiants d'images est obligatoire")
        List<Long> imageIds
) {}
