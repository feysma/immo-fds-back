package be.feysdigitalservices.immofds.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record VisitRequestDto(
        @NotBlank(message = "Le prénom est obligatoire")
        String firstName,

        @NotBlank(message = "Le nom est obligatoire")
        String lastName,

        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "L'email n'est pas valide")
        String email,

        String phone,

        String message,

        @NotBlank(message = "La référence du bien est obligatoire")
        String propertyReference
) {}
