package be.feysdigitalservices.immofds.dto.request;

import be.feysdigitalservices.immofds.domain.enums.ContactStatus;
import jakarta.validation.constraints.NotNull;

public record ContactStatusUpdateRequest(
        @NotNull(message = "Le statut est obligatoire")
        ContactStatus status
) {}
