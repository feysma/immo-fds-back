package be.feysdigitalservices.immofds.dto.request;

import be.feysdigitalservices.immofds.domain.enums.*;
import be.feysdigitalservices.immofds.validation.BelgianPostalCode;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record PropertyCreateRequest(
        @NotBlank(message = "Le titre est obligatoire")
        String title,

        String description,

        @NotNull(message = "Le type de bien est obligatoire")
        PropertyType propertyType,

        @NotNull(message = "Le type de transaction est obligatoire")
        TransactionType transactionType,

        @NotNull(message = "Le prix est obligatoire")
        @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être positif")
        BigDecimal price,

        @PositiveOrZero(message = "La surface doit être positive ou nulle")
        Double surface,

        @Min(value = 0, message = "Le nombre de chambres doit être positif ou nul")
        Integer bedrooms,

        @Min(value = 0, message = "Le nombre de salles de bain doit être positif ou nul")
        Integer bathrooms,

        @Min(value = 0, message = "Le nombre de pièces doit être positif ou nul")
        Integer rooms,

        @Min(value = 0, message = "Le nombre d'étages doit être positif ou nul")
        Integer floors,

        @Min(value = 1800, message = "L'année de construction doit être supérieure à 1800")
        Integer constructionYear,

        EnergyRating energyRating,

        boolean garden,
        boolean garage,
        boolean terrace,
        boolean basement,
        boolean elevator,
        boolean furnished,

        @NotBlank(message = "La rue est obligatoire")
        String street,

        String number,

        @NotBlank(message = "Le code postal est obligatoire")
        @BelgianPostalCode
        String postalCode,

        @NotBlank(message = "La ville est obligatoire")
        String city,

        @NotNull(message = "La province est obligatoire")
        Province province,

        Double latitude,
        Double longitude
) {}
