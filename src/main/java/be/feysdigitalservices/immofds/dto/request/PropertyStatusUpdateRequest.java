package be.feysdigitalservices.immofds.dto.request;

import be.feysdigitalservices.immofds.domain.enums.PropertyStatus;
import jakarta.validation.constraints.NotNull;

public record PropertyStatusUpdateRequest(
        @NotNull(message = "Le statut est obligatoire")
        PropertyStatus status
) {}
