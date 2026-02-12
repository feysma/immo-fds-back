package be.feysdigitalservices.immofds.dto.request;

import be.feysdigitalservices.immofds.domain.enums.PropertyType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record SellYourHomeRequest(
        @NotBlank(message = "Le prénom est obligatoire")
        String firstName,

        @NotBlank(message = "Le nom est obligatoire")
        String lastName,

        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "L'email n'est pas valide")
        String email,

        String phone,

        String message,

        @NotBlank(message = "L'adresse du bien est obligatoire")
        String propertyAddress,

        @NotNull(message = "Le type de bien est obligatoire")
        PropertyType propertyType,

        @DecimalMin(value = "0.0", inclusive = false, message = "Le prix estimé doit être positif")
        BigDecimal estimatedPrice
) {}
